package com.ckw.lightweightmusicplayer.ui.localmusic.fragments;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.SizeUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.repository.Artist;
import com.ckw.lightweightmusicplayer.repository.Song;
import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalArtistViewHolder;
import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalSongViewHolder;
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

public class LocalMusicListFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks{
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

    }

    @Override
    protected void handleBundle(Bundle bundle) {
        mTitle = bundle.getString(ARG_TITLE);
    }

    @Override
    protected void operateViews(View view) {
        requestPermission();
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
        }else {
            mEasyRecyclerView.setAdapter(mArtistAdapter = new RecyclerArrayAdapter<Artist>(getContext()) {
                @Override
                public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                    return new LocalArtistViewHolder(parent,getContext());
                }
            });
            mArtistAdapter.addAll(mArtists);
            for (int i = 0; i < mArtists.size(); i++) {
                Log.d("----", "initEasyRecyclerView: "+mArtists.get(i).toString());
            }
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode){
            case REQUEST_READ_EXTERNAL_STORAGE:
                getLocalSongs();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requestPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            getLocalSongs();
        } else {
            EasyPermissions.requestPermissions(this,"本地音乐需要读取内存权限",REQUEST_READ_EXTERNAL_STORAGE,perms);
        }

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



}
