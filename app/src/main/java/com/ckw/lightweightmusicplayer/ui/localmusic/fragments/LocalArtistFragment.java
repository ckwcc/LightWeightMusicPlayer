package com.ckw.lightweightmusicplayer.ui.localmusic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.ui.localmusic.activities.ArtistActivity;
import com.ckw.lightweightmusicplayer.ui.localmusic.adapter.AlbumListAdapter;
import com.ckw.lightweightmusicplayer.ui.localmusic.adapter.ArtistListAdapter;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper.MEDIA_ID_ARTIST;


/**
 * Created by ckw
 * on 2018/4/8.
 */
public class LocalArtistFragment extends BaseFragment {

    @Inject
    public LocalArtistFragment() {
    }

    @BindView(R.id.recyclerView_artist)
    EasyRecyclerView mEasyRecyclerView;

    private List<MediaBrowserCompat.MediaItem> mArtists;//歌手
    private RecyclerArrayAdapter<MediaBrowserCompat.MediaItem> mAdapter;
    private String mMediaId;
    private MediaBrowserCompat mMediaBrowser;

    public void onConnected(){
        if (isDetached()) {
            return;
        }
        mMediaId = MEDIA_ID_ARTIST;
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
            mAdapter.clear();
            mAdapter.addAll(children);
            mAdapter.notifyDataSetChanged();

            mArtists.clear();
            mArtists.addAll(children);

        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowser = mediaBrowserProvider.getMediaBrowser();

        if(mMediaBrowser.isConnected()){
            onConnected();
        }
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_local_artist;
    }

    @Override
    protected void initVariables() {
        mArtists = new ArrayList<>();
    }

    @Override
    protected void handleBundle(Bundle bundle) {

    }

    @Override
    protected void operateViews(View view) {
        initEasyRecyclerView();
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MediaBrowserCompat.MediaItem mediaItem = mArtists.get(position);
                String artistName = mediaItem.getDescription().getMediaId();
                Intent intent = new Intent(getContext(), ArtistActivity.class);
                intent.putExtra("artistName",artistName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initPresenter() {

    }

    private void initEasyRecyclerView() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2, LinearLayoutManager.VERTICAL,false);
        mEasyRecyclerView.setLayoutManager(gridLayoutManager);

        SpaceDecoration itemDecoration = new SpaceDecoration((int) SizeUtils.dp2px(4));//参数是距离宽度
        itemDecoration.setPaddingEdgeSide(true);//是否为左右2边添加padding.默认true.
        itemDecoration.setPaddingStart(true);//是否在给第一行的item添加上padding(不包含header).默认true.
        itemDecoration.setPaddingHeaderFooter(false);//是否对Header于Footer有效,默认false.
        mEasyRecyclerView.addItemDecoration(itemDecoration);

        mAdapter = new ArtistListAdapter(getContext());

        mEasyRecyclerView.setAdapter(mAdapter);


    }
}
