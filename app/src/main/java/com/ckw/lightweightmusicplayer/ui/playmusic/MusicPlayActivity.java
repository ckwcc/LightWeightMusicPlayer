package com.ckw.lightweightmusicplayer.ui.playmusic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.blankj.utilcode.util.FragmentUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;

import javax.inject.Inject;

/**
 * Created by ckw
 * on 2018/3/14.
 */

public class MusicPlayActivity extends BaseActivity {

    @Inject
    MusicPlayFragment musicPlayFragment;

    private FragmentManager manager;

    private String mediaId;

    @Override
    protected void onMediaControllerConnected() {
        super.onMediaControllerConnected();
        if(mediaId != null){
            MediaControllerCompat.getMediaController(this).getTransportControls()
                    .playFromMediaId(mediaId, null);
        }
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        FragmentUtils.add(manager,musicPlayFragment,R.id.cl_content);


    }

    @Override
    protected void handleBundle(@NonNull Bundle bundle) {
        mediaId = bundle.getString("musicId");
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
