package com.ckw.lightweightmusicplayer.ui.localmusic.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.repository.RecentBean;
import com.ckw.lightweightmusicplayer.repository.RecentlyPlayed;
import com.ckw.lightweightmusicplayer.repository.Song;
import com.ckw.lightweightmusicplayer.ui.localmusic.adapter.MusicListAdapter;
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity;
import com.ckw.lightweightmusicplayer.utils.RecentUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper.MEDIA_ID_NORMAL;


/**
 * Created by ckw
 * on 2018/3/12.
 * 本地音乐-歌曲列表 、歌手列表
 */

public class LocalMusicListFragment extends BaseFragment{

    @Inject
    public LocalMusicListFragment() {
    }

    @BindView(R.id.recyclerView)
    EasyRecyclerView mEasyRecyclerView;

    private RecyclerArrayAdapter<MediaBrowserCompat.MediaItem> mAdapter;

    private List<MediaBrowserCompat.MediaItem> mSongs;//歌曲

    private String mMediaId;//分类id

    private MediaBrowserCompat mMediaBrowser;


    @Override
    public void initPresenter() {
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_local_music;
    }

    @Override
    protected void initVariables() {
        mSongs = new ArrayList<>();

    }

    @Override
    protected void handleBundle(Bundle bundle) {
    }

    @Override
    protected void operateViews(View view) {
        initEasyRecyclerView();
    }

    private void initEasyRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mEasyRecyclerView.setLayoutManager(linearLayoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, SizeUtils.dp2px(1), SizeUtils.dp2px(8),0);
        itemDecoration.setDrawLastItem(false);
        mEasyRecyclerView.addItemDecoration(itemDecoration);

        mAdapter = new MusicListAdapter(getContext());
        mEasyRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(position == -1){
                    return;
                }
                MediaBrowserCompat.MediaItem mediaItem = mSongs.get(position);

//                RecentUtils.addToRecent(mediaItem);

                Uri iconUri = mediaItem.getDescription().getIconUri();

                Bundle bundle = new Bundle();
                bundle.putString("musicId", mediaItem.getMediaId());
                if(iconUri != null){
                    bundle.putString("iconUri",iconUri.toString());
                }
                bundle.putBoolean("play",true);
                ActivityUtils.startActivity(bundle,MusicPlayActivity.class);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //在这里实际上拿到的mMediaBrowser不是空，但是没有连接着，google sample中也是如此
        mMediaBrowser = mediaBrowserProvider.getMediaBrowser();

        if(mMediaBrowser.isConnected()){
            onConnected();
        }
    }


    public void onConnected(){
        if (isDetached()) {
            return;
        }
        mMediaId = MEDIA_ID_NORMAL;
        mediaBrowserProvider.getMediaBrowser().unsubscribe(mMediaId);
        mediaBrowserProvider.getMediaBrowser().subscribe(mMediaId,mSubscriptionCallback);
    }




    /*
    * 浏览器订阅的接口，数据的回调
    * */
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            //children 即为Service发送回来的媒体数据集合
            //在onChildrenLoaded可以执行刷新列表UI的操作
            //这里需要用adapter直接加数据源，用上面的代码，无效，估计是这个EasyRecyclerView框架的问题
            mSongs.clear();
            mSongs.addAll(children);
            mAdapter.clear();
            mAdapter.addAll(children);
            mAdapter.notifyDataSetChanged();

        }
    };





}
