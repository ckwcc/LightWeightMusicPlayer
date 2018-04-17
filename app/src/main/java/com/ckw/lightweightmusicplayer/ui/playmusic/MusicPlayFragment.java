package com.ckw.lightweightmusicplayer.ui.playmusic;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.View;
import android.widget.ImageView;

import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.weight.cover_view.MusicCoverView;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by ckw
 * on 2018/3/14.
 */

public class MusicPlayFragment extends BaseFragment {


    @Inject
    public MusicPlayFragment() {
    }

    @BindView(R.id.cover)
    MusicCoverView musicCoverView;
    @BindView(R.id.fab)
    FloatingActionButton mStop;
    @BindView(R.id.previous)
    ImageView mSkipToPrevious;//上一首
    @BindView(R.id.next)
    ImageView mSkipToNext;//下一首
    @BindView(R.id.repeat)
    ImageView mRepeatMode;//循环模式

    private MediaControllerCompat.TransportControls mController;

    public void setController(MediaControllerCompat.TransportControls controller) {
        this.mController = controller;
    }

    public void setMusicCoverViewStart(){
        musicCoverView.start();
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

        mSkipToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mController != null){
                    mController.skipToPrevious();
                }
            }
        });

        mSkipToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mController != null){
                    mController.skipToNext();
                }
            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mController != null){
                    mController.stop();
                }
                musicCoverView.stop();
            }
        });

        musicCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {

            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                getActivity().supportFinishAfterTransition();
            }
        });
    }

   
}
