package com.ckw.lightweightmusicplayer.ui.playmusic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.blankj.utilcode.util.FragmentUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;

/**
 * Created by ckw
 * on 2018/3/14.
 */

public class MusicPlayActivity extends BaseActivity {

    private MusicPlayFragment musicPlayFragment;
    private FragmentManager manager;

    @Override
    protected void initView(Bundle savedInstanceState) {
        musicPlayFragment = (MusicPlayFragment) manager.findFragmentById(R.id.cl_content);
        if(musicPlayFragment == null){
            musicPlayFragment = MusicPlayFragment.newInstance();
            FragmentUtils.add(manager,musicPlayFragment,R.id.cl_content);
        }
    }

    @Override
    protected void handleBundle(@NonNull Bundle bundle) {

    }

    @Override
    protected void initVariable() {
        manager = getSupportFragmentManager();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_music_play;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected boolean needToolbar() {
        return false;
    }

    @Override
    public void setToolbar() {

    }
}
