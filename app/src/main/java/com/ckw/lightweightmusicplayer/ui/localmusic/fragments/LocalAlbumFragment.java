package com.ckw.lightweightmusicplayer.ui.localmusic.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.SizeUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalAlbumViewHolder;
import com.ckw.lightweightmusicplayer.utils.MediaUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ckw
 * on 2018/3/13.
 * 本地音乐-专辑
 */

public class LocalAlbumFragment extends BaseFragment{

    @BindView(R.id.recyclerView_album)
    EasyRecyclerView mEasyRecyclerView;

    private List<Album> mAlbums;//专辑
    private RecyclerArrayAdapter<Album> mAdapter;

    public static LocalAlbumFragment newInstance() {
        Bundle args = new Bundle();
        LocalAlbumFragment fragment = new LocalAlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_local_album;
    }

    @Override
    protected void initVariables() {
        mAlbums = new ArrayList<>();
        getLocalSongs();
    }

    @Override
    protected void handleBundle(Bundle bundle) {

    }

    @Override
    protected void operateViews(View view) {

    }

    @Override
    protected void initListener() {
    }

    /*
   * 获取本地音乐信息
   * */
    private void getLocalSongs(){
        mAlbums.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAlbums = MediaUtils.getAlbumList(getContext());
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();


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

    private void initEasyRecyclerView() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,LinearLayoutManager.VERTICAL,false);
        mEasyRecyclerView.setLayoutManager(gridLayoutManager);

        SpaceDecoration itemDecoration = new SpaceDecoration((int) SizeUtils.dp2px(4));//参数是距离宽度
        itemDecoration.setPaddingEdgeSide(true);//是否为左右2边添加padding.默认true.
        itemDecoration.setPaddingStart(true);//是否在给第一行的item添加上padding(不包含header).默认true.
        itemDecoration.setPaddingHeaderFooter(false);//是否对Header于Footer有效,默认false.
        mEasyRecyclerView.addItemDecoration(itemDecoration);

        mEasyRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Album>(getContext()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new LocalAlbumViewHolder(parent,getContext(),getActivity());
            }
        });

        mAdapter.addAll(mAlbums);

    }
}
