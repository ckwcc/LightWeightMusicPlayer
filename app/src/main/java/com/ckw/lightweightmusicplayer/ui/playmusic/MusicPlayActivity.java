package com.ckw.lightweightmusicplayer.ui.playmusic;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.blankj.utilcode.util.FragmentUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;


import javax.inject.Inject;

/**
 * Created by ckw
 * on 2018/3/14.
 */

public class MusicPlayActivity extends BaseActivity {

    public static final String EXTRA_START_FULLSCREEN =
            "com.example.android.uamp.EXTRA_START_FULLSCREEN";

    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "com.example.android.uamp.CURRENT_MEDIA_DESCRIPTION";

    @Inject
    MusicPlayFragment musicPlayFragment;

    private FragmentManager manager;

    private String mediaId;
    private boolean shouldPlay;
    private MediaControllerCompat.TransportControls mController;
    private MediaControllerCompat mediaControllerCompat;

    @Override
    protected void onMediaControllerConnected(MediaSessionCompat.Token token) {
        super.onMediaControllerConnected(token);
        if(mediaId != null){
            try {
                 mediaControllerCompat = new MediaControllerCompat(
                        MusicPlayActivity.this, token);
                 mController = mediaControllerCompat.getTransportControls();
                 if(shouldPlay){
                     mController
                             .playFromMediaId(mediaId, null);
                 }
                musicPlayFragment.setMediaControllerCompat(mediaControllerCompat,shouldPlay);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        FragmentUtils.add(manager,musicPlayFragment,R.id.cl_content);

    }

    @Override
    protected void handleBundle(@NonNull Bundle bundle) {
        mediaId = bundle.getString("musicId");
        shouldPlay = bundle.getBoolean("play");
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

    @Override
    protected void onStop() {
        super.onStop();
        shouldPlay = false;
    }
}
