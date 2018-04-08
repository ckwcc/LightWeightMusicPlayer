package com.ckw.lightweightmusicplayer.ui.localmusic.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.repository.Artist;
import com.ckw.lightweightmusicplayer.repository.Song;
import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalArtistViewHolder;
import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalSongViewHolder;
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity;
import com.ckw.lightweightmusicplayer.ui.playmusic.service.MusicService;
import com.ckw.lightweightmusicplayer.utils.MediaUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ckw
 * on 2018/3/12.
 * 本地音乐-歌曲列表 、歌手列表
 */

public class LocalMusicListFragment extends BaseFragment{
    private static final String ARG_TITLE = "title";
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    @BindView(R.id.recyclerView)
    EasyRecyclerView mEasyRecyclerView;

    private RecyclerArrayAdapter<Song> mAdapter;
    private RecyclerArrayAdapter<Artist> mArtistAdapter;

    private List<Song> mSongs;//歌曲
    private List<Album> mAlbums;//专辑
    private List<Artist> mArtists;//歌手

    private String mTitle;//歌曲，专辑，歌手

    private MediaBrowserCompat mMediaBrowser;

    public static LocalMusicListFragment newInstance(String title) {
        LocalMusicListFragment fragment = new LocalMusicListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

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
        mAlbums = new ArrayList<>();
        mArtists = new ArrayList<>();
        getLocalSongs();
        mMediaBrowser = new MediaBrowserCompat(getActivity(),
                new ComponentName(getActivity(), MusicService.class),
                mConnectionCallback,
                null);
    }

    @Override
    protected void handleBundle(Bundle bundle) {
        mTitle = bundle.getString(ARG_TITLE);
    }

    @Override
    protected void operateViews(View view) {

    }

    private void initEasyRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mEasyRecyclerView.setLayoutManager(linearLayoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, SizeUtils.dp2px(1), SizeUtils.dp2px(8),0);
        itemDecoration.setDrawLastItem(false);
        mEasyRecyclerView.addItemDecoration(itemDecoration);



        if("歌曲".equals(mTitle)){
            mEasyRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Song>(getContext()) {
                @Override
                public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                    return new LocalSongViewHolder(parent,getContext());
                }
            });
            mAdapter.addAll(mSongs);

            mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    ActivityUtils.startActivity(MusicPlayActivity.class);
                }
            });
        }else {
            mEasyRecyclerView.setAdapter(mArtistAdapter = new RecyclerArrayAdapter<Artist>(getContext()) {
                @Override
                public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                    return new LocalArtistViewHolder(parent,getContext());
                }
            });
            mArtistAdapter.addAll(mArtists);

        }

    }

    @Override
    protected void initListener() {

    }



    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowser.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaBrowser.disconnect();
    }

    /*
    * 获取本地音乐信息
    * */
    private void getLocalSongs(){
        mSongs.clear();
        mAlbums.clear();
        mArtists.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mSongs = MediaUtils.getAudioList(getContext());
                mAlbums = MediaUtils.getAlbumList(getContext());
                if (mSongs != null) {
                    for (Song song : mSongs) {
                        song.setAlbumObj(getAlbum(song.getAlbumId()));
                    }
                }

                mArtists = MediaUtils.getArtistList(getContext());

                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();


    }

    private Album getAlbum (int albumId) {
        for (Album album : mAlbums) {
            if (album.getId() == albumId) {
                return album;
            }
        }
        return null;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    initEasyRecyclerView();
                    break;
            }
            return false;
        }
    });


    /*
    * 浏览器连接的回调
    * */
    private MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback(){
        @Override
        public void onConnected() {
            super.onConnected();
            if(mMediaBrowser.isConnected()){
                String mediaId = mMediaBrowser.getRoot();
                mMediaBrowser.unsubscribe(mediaId);
                mMediaBrowser.subscribe(mediaId, mSubscriptionCallback);
            }
        }
    };

    /*
    * 浏览器订阅的接口，数据的回调
    * */
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            Log.d("----", "onChildrenLoaded: 请求数据:"+children.size());
            //children 即为Service发送回来的媒体数据集合
            for (MediaBrowserCompat.MediaItem item:children){
                Log.d("----", "onChildrenLoaded: "+item.getDescription().getTitle()+";url:"+item.getDescription().getMediaUri());
            }
            //在onChildrenLoaded可以执行刷新列表UI的操作
        }
    };



}
