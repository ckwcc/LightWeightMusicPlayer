package com.ckw.lightweightmusicplayer.ui.playmusic;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.ui.playmusic.service.MusicService;
import com.ckw.lightweightmusicplayer.weight.ProgressView;
import com.ckw.lightweightmusicplayer.weight.cover_view.MusicCoverView;

import javax.inject.Inject;

import butterknife.BindView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by ckw
 * on 2018/3/14.
 */

public class MusicPlayFragment extends BaseFragment {


    @Inject
    public MusicPlayFragment() {
    }

    @BindView(R.id.cover)
    MusicCoverView musicCoverView;//旋转view
    @BindView(R.id.progress)
    ProgressView mProgressView;//进度条
    @BindView(R.id.fab)
    FloatingActionButton mFab;//开始/暂停
    @BindView(R.id.previous)
    ImageView mSkipToPrevious;//上一首
    @BindView(R.id.next)
    ImageView mSkipToNext;//下一首
    @BindView(R.id.repeat)
    ImageView mRepeatMode;//循环模式

    private PlaybackStateCompat mLastPlaybackState;

    private boolean isPlaying = true;

    private MediaControllerCompat mediaControllerCompat;
    private MediaControllerCompat.TransportControls mController;


    public void setMediaControllerCompat(MediaControllerCompat mediaControllerCompat) {
        this.mediaControllerCompat = mediaControllerCompat;
        mController = mediaControllerCompat.getTransportControls();
        this.mediaControllerCompat.registerCallback(mMediaControllerCallback);

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

    // Callback that ensures that we are showing the controls
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    updatePlaybackState(state);
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {

                }
            };

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }

        mLastPlaybackState = state;
        switch (state.getState()){
            case PlaybackStateCompat.STATE_PLAYING:
                mFab.setImageResource(android.R.drawable.ic_media_pause);
//                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mFab.setImageResource(android.R.drawable.ic_media_play);
//                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
//                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
//                stopSeekbarUpdate();
                break;
            default:

        }
    }

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        mProgressView.setProgress((int) currentPosition);
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

        mRepeatMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mController != null){
                    if(isPlaying){
                        mController.pause();
                        musicCoverView.stop();
                        isPlaying = false;
                    }else {
                        mController.play();
                        musicCoverView.start();
                        isPlaying = true;
                    }
                }
            }
        });

        musicCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {

            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {

                isPlaying = false;
            }
        });
    }

   
}
