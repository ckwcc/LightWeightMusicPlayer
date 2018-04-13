package com.ckw.lightweightmusicplayer.ui.localmusic.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;

import com.blankj.utilcode.util.SizeUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;
import com.ckw.lightweightmusicplayer.ui.localmusic.adapter.MusicListAdapter;
import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;

/**
 * Created by ckw
 * on 2018/4/13.
 */
public class ArtistActivity extends BaseActivity{

    @BindView(R.id.ctl_artist)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.rv_artist)
    EasyRecyclerView mEasyRecyclerView;

    private RecyclerArrayAdapter<MediaBrowserCompat.MediaItem> mAdapter;

    private String mediaId;
    private String mArtistName;

    @Override
    protected void initView(Bundle savedInstanceState) {
        initRecyclerView();
        if(mArtistName != null){
            mCollapsingToolbarLayout.setTitle(mArtistName);
            mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        }
    }

    @Override
    protected void handleBundle(@NonNull Bundle bundle) {
        mArtistName = bundle.getString("artistName");
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_artist;
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

    @Override
    protected void onMediaBrowserConnected() {
        super.onMediaBrowserConnected();
        boolean connected = mMediaBrowser.isConnected();
        mediaId = MediaIdHelper.MEDIA_ID_ARTIST_DETAIL +"&"+ mArtistName;
        if(connected){
            mMediaBrowser.unsubscribe(mediaId);
            mMediaBrowser.subscribe(mediaId,mSubscriptionCallback);
        }
    }

    /*
     * 浏览器订阅的接口，数据的回调
     * */
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            mAdapter.clear();
            mAdapter.addAll(children);
            mAdapter.notifyDataSetChanged();

        }
    };

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mEasyRecyclerView.setLayoutManager(linearLayoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, SizeUtils.dp2px(1), SizeUtils.dp2px(8),0);
        itemDecoration.setDrawLastItem(false);
        mEasyRecyclerView.addItemDecoration(itemDecoration);

        mAdapter = new MusicListAdapter(getApplicationContext());
        mEasyRecyclerView.setAdapter(mAdapter);
    }
}
