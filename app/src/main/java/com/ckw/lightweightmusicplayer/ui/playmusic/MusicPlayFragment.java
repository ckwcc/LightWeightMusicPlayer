package com.ckw.lightweightmusicplayer.ui.playmusic;

import android.os.Bundle;
import android.view.View;

import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;

/**
 * Created by ckw
 * on 2018/3/14.
 */

public class MusicPlayFragment extends BaseFragment {

    public static MusicPlayFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MusicPlayFragment fragment = new MusicPlayFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void initPresenter() {

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_music_play;
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
}
