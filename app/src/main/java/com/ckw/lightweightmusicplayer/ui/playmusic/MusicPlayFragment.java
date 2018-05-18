package com.ckw.lightweightmusicplayer.ui.playmusic;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.weight.ProgressView;
import com.ckw.lightweightmusicplayer.weight.cover_view.MusicCoverView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;


/**
 * Created by ckw
 * on 2018/3/14.
 */

public class MusicPlayFragment extends BaseFragment {

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    public static final int REPEAT_MODE_DEFAULT = 2;
    public static final int REPEAT_MODE_SINGLE = 1;
    private int mSongDuration;

    @Inject
    public MusicPlayFragment() {
    }

    @BindView(R.id.cover)
    MusicCoverView musicCoverView;//旋转view
    @BindView(R.id.progress)
    ProgressView mProgressView;//进度条
    @BindView(R.id.time)
    TextView mCurrentTime;
    @BindView(R.id.duration)
    TextView mTotalTime;
    @BindView(R.id.fab)
    FloatingActionButton mFab;//开始/暂停
    @BindView(R.id.previous)
    ImageView mSkipToPrevious;//上一首
    @BindView(R.id.next)
    ImageView mSkipToNext;//下一首
    @BindView(R.id.repeat)
    ImageView mIvRepeat;//循环模式
    @BindView(R.id.tv_song_name)
    TextView mSongName;
    @BindView(R.id.tv_song_artist)
    TextView mSongArtist;

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mScheduleFuture;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private PlaybackStateCompat mLastPlaybackState;

    private boolean isPlaying = true;

    private MediaControllerCompat mediaControllerCompat;
    private MediaControllerCompat.TransportControls mController;

    public void setMediaControllerCompat(MediaControllerCompat mediaControllerCompat,boolean shouldPlay,String iconUri) {
        this.mediaControllerCompat = mediaControllerCompat;
        mController = mediaControllerCompat.getTransportControls();
        this.mediaControllerCompat.registerCallback(mMediaControllerCallback);

        MediaMetadataCompat metadata = this.mediaControllerCompat.getMetadata();

        PlaybackStateCompat state = this.mediaControllerCompat.getPlaybackState();

        if(state != null){
            updatePlaybackState(state);
        }

        if(metadata != null){
            updateDuration(metadata);
        }

        if(iconUri != null && !iconUri.equals("")){
            //图片显示的大小还有问题，后期考虑换一种显示view
//            Glide.with(getContext()).load(iconUri)
//                    .into(musicCoverView);
        }

        if(shouldPlay){
            updateProgress();
        }else {
            musicCoverView.stop();
        }

        if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }

        setRepeatMode(false);
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
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mExecutorService.shutdown();
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

        mIvRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeatMode(true);
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaControllerCompat != null){
                    PlaybackStateCompat state = mediaControllerCompat.getPlaybackState();
                    if (state != null) {
                        switch (state.getState()) {
                            case PlaybackStateCompat.STATE_PLAYING: // fall through
                            case PlaybackStateCompat.STATE_BUFFERING:
                                isPlaying = false;
                                mController.pause();
                                stopSeekbarUpdate();
                                if(mFab != null){
                                    mFab.setImageResource(android.R.drawable.ic_media_play);
                                }
                                musicCoverView.stop();
                                break;
                            case PlaybackStateCompat.STATE_PAUSED:
                            case PlaybackStateCompat.STATE_STOPPED:
                                isPlaying = true;
                                mController.play();
                                scheduleSeekbarUpdate();
                                if(mFab != null){
                                    mFab.setImageResource(android.R.drawable.ic_media_pause);
                                }
                                musicCoverView.start();
                                break;
                            default:
                        }
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

    @Override
    public void onResume() {
        super.onResume();
        if (!isPlaying){
            if(mFab != null){
                mFab.setImageResource(android.R.drawable.ic_media_play);
            }
        }else {
            if(mFab != null){
                mFab.setImageResource(android.R.drawable.ic_media_pause);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaControllerCompat != null){
            mediaControllerCompat.unregisterCallback(mMediaControllerCallback);
        }
    }

    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {

                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    updatePlaybackState(state);
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    updateDuration(metadata);
                }
            };

    /*
    * 更新播放状态
    * */
    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }

        mLastPlaybackState = state;

        switch (state.getState()){
            case PlaybackStateCompat.STATE_PLAYING:
                if(mFab != null){
                    mFab.setImageResource(android.R.drawable.ic_media_pause);
                }
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                stopSeekbarUpdate();
                if(mFab != null){
                    mFab.setImageResource(android.R.drawable.ic_media_play);
                }
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                stopSeekbarUpdate();
                break;
            default:

        }
    }

    /*
     * 更新进度条
     * */
    private void updateProgress() {
        musicCoverView.start();
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();

        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {

            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }

        if(currentPosition > (mSongDuration + 1000) && SPUtils.getInstance().getInt("repeat", REPEAT_MODE_DEFAULT) == REPEAT_MODE_SINGLE){
            //这里的目的是为了让单曲循环时，控制进度条和正在播放的时间正确
            currentPosition = 0;
            mController.pause();
            mController.play();
        }else if(currentPosition > (mSongDuration + 1000) && SPUtils.getInstance().getInt("repeat", REPEAT_MODE_DEFAULT) == REPEAT_MODE_DEFAULT){
            mController.skipToNext();
        }
        mCurrentTime.setText(DateUtils.formatElapsedTime(currentPosition / 1000));
        if(mProgressView != null){
            mProgressView.setProgress((int) currentPosition);
        }
    }

    /*
    * 控制进度条
    * */
    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    /*
    * 更新时间信息
    * */
    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        mSongDuration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
        String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
        if(mSongName != null){
            mSongName.setText(title);
        }
        if(mSongArtist != null){
            mSongArtist.setText(artist);
        }
        if(mProgressView != null){
            mProgressView.setMax(mSongDuration);
        }
        if(mTotalTime != null){
            mTotalTime.setText(DateUtils.formatElapsedTime(mSongDuration /1000));
        }
    }

    /*
    * 设置循环模式
    * */
    private void setRepeatMode(boolean isClick){
        if(isClick){//通过点击
            int repeat = SPUtils.getInstance().getInt("repeat", REPEAT_MODE_DEFAULT);
            if(REPEAT_MODE_DEFAULT == (repeat)){//一开始是默认的，点击之后变成单曲
                mIvRepeat.setImageResource(R.mipmap.ic_repeat_single);
                SPUtils.getInstance().put("repeat",REPEAT_MODE_SINGLE);
                mController.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
            }else {
                mIvRepeat.setImageResource(R.mipmap.ic_repeat_white_24dp);
                SPUtils.getInstance().put("repeat",REPEAT_MODE_DEFAULT);
                mController.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
            }
        }else {//初始化
            int repeat = SPUtils.getInstance().getInt("repeat", REPEAT_MODE_DEFAULT);
            if(REPEAT_MODE_DEFAULT == repeat){
                mIvRepeat.setImageResource(R.mipmap.ic_repeat_white_24dp);
                SPUtils.getInstance().put("repeat",REPEAT_MODE_DEFAULT);
                mController.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
            }else {
                mIvRepeat.setImageResource(R.mipmap.ic_repeat_single);
                SPUtils.getInstance().put("repeat",REPEAT_MODE_SINGLE);
                mController.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
            }
        }

    }

}
