package com.ckw.lightweightmusicplayer.ui.localmusic.fragments;

import android.os.Bundle;
import android.view.View;

import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;


/**
 * Created by ckw
 * on 2018/4/8.
 */
public class LocalArtistFragment extends BaseFragment {

    public static LocalArtistFragment newInstance() {

        Bundle args = new Bundle();

        LocalArtistFragment fragment = new LocalArtistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_local_artist;
    }

    @Override
    protected void initVariables() {

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

    @Override
    public void initPresenter() {

    }
}
