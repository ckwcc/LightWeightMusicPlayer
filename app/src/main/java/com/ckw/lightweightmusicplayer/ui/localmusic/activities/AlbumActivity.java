package com.ckw.lightweightmusicplayer.ui.localmusic.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.repository.Song;
import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalSongViewHolder;
import com.ckw.lightweightmusicplayer.utils.MediaUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;

/**
 * Created by ckw
 * on 2018/3/14.
 * 专辑 详情界面
 */

public class AlbumActivity extends BaseActivity {

    @BindView(R.id.iv_album)
    ImageView mPhotoAlbum;
    @BindView(R.id.ctl_album)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.rv_album)
    EasyRecyclerView mEasyRecyclerView;

    private Album mAlbum;
    private List<Song> mSongs;
    private RecyclerArrayAdapter<Song> mAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        if(mAlbum != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mSongs = MediaUtils.getAlbumSongList(AlbumActivity.this,mAlbum.getId());
                    List<Album> mTotal = MediaUtils.getAlbumList(AlbumActivity.this);
                    for (Song song : mSongs) {
                        song.setAlbumObj(getAlbum(song.getAlbumId(),mTotal));
                    }

                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);

                }
            }).start();


            mCollapsingToolbarLayout.setTitle(mAlbum.getAlbum());
            mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
            Glide.with(this).load(mAlbum.getAlbumArt()).into(mPhotoAlbum);
        }
    }

    @Override
    protected void handleBundle(@NonNull Bundle bundle) {
        mAlbum = (Album)bundle.getSerializable("album");
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_album;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    public void setToolbar() {

    }

    private Album getAlbum (int albumId,List<Album> mTotalAlbumList) {
        for (Album album : mTotalAlbumList) {
            if (album.getId() == albumId) {
                return album;
            }
        }
        return null;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    initRecyclerView();
                    break;
            }
            return false;
        }
    });

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mEasyRecyclerView.setLayoutManager(linearLayoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, SizeUtils.dp2px(1), SizeUtils.dp2px(8),0);
        itemDecoration.setDrawLastItem(false);
        mEasyRecyclerView.addItemDecoration(itemDecoration);

        mEasyRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Song>(this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new LocalSongViewHolder(parent,getContext());
            }
        });
        mAdapter.addAll(mSongs);
    }
}
