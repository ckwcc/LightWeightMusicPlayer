package com.ckw.lightweightmusicplayer.ui.playmusic;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.repository.RecentBean;
import com.ckw.lightweightmusicplayer.utils.RecentUtils;
import com.ckw.lightweightmusicplayer.weight.ProgressView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ckw
 * on 2018/3/14.
 */

public class MusicPlayFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_CODE_PICTURE = 3;
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    public static final int REPEAT_MODE_DEFAULT = 2;
    public static final int REPEAT_MODE_SINGLE = 1;
    private int mSongDuration;
    private String mediaId;
    private String mArtist;
    private String mTitle;
    private String mAlbum;
    private ValueAnimator startRotateAnimator;

    @Inject
    public MusicPlayFragment() {
    }

    @BindView(R.id.cover)
    CircleImageView musicCoverView;//旋转view

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
    @BindView(R.id.iv_favorite)
    ImageView mIvFavorite;//我喜欢的
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
        startRotateAnimator.end();
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
        mIvFavorite.setOnClickListener(this);
        mSkipToPrevious.setOnClickListener(this);
        mSkipToNext.setOnClickListener(this);
        mIvRepeat.setOnClickListener(this);
        mFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.iv_favorite:
                if(RecentUtils.isFavorite(mediaId)){//是我的喜欢
                    mIvFavorite.setImageResource(R.mipmap.ic_favorite_default);
                    RecentUtils.removeTheFavorite(mediaId);
                }else {
                    mIvFavorite.setImageResource(R.mipmap.ic_favorite);
                    RecentBean recentBean = new RecentBean();
                    recentBean.setMediaId(mediaId);
                    recentBean.setTitle(mTitle);
                    recentBean.setArtist(mArtist);
                    if(mAlbum != null){
                        recentBean.setAlbum(mAlbum);
                    }
                    RecentUtils.addToFavorite(recentBean);
                }
                break;
            case R.id.fab:
                if(mediaControllerCompat != null){
                    PlaybackStateCompat state = mediaControllerCompat.getPlaybackState();
                    if (state != null) {
                        switch (state.getState()) {
                            case PlaybackStateCompat.STATE_PLAYING:
                            case PlaybackStateCompat.STATE_BUFFERING:
                                isPlaying = false;
                                mController.pause();
                                stopSeekbarUpdate();
                                if(mFab != null){
                                    mFab.setImageResource(android.R.drawable.ic_media_play);
                                }
                                stopAnimation();

                                break;
                            case PlaybackStateCompat.STATE_PAUSED:
                            case PlaybackStateCompat.STATE_STOPPED:
                                isPlaying = true;
                                mController.play();
                                scheduleSeekbarUpdate();
                                if(mFab != null){
                                    mFab.setImageResource(android.R.drawable.ic_media_pause);
                                }
                                restartAnimation();
                                break;
                            default:
                        }
                    }
                }
                break;
            case R.id.next:
                if(mController != null){
                    mController.skipToNext();
                }
                break;
            case R.id.previous:
                if(mController != null){
                    mController.skipToPrevious();
                }
                break;
            case R.id.repeat:
                setRepeatMode(true);
                break;
        }
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


    public void setMediaControllerCompat(MediaControllerCompat mediaControllerCompat, boolean shouldPlay, String iconUri, String mediaId) {
        this.mediaId = mediaId;
        this.mAlbum = iconUri;
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


        if (RecentUtils.isFavorite(mediaId)) {
            mIvFavorite.setImageResource(R.mipmap.ic_favorite);
        }else {
            mIvFavorite.setImageResource(R.mipmap.ic_favorite_default);
        }

        if(shouldPlay){
            updateProgress();
            showRotateImageView();
            startAnimation(musicCoverView);
        }

        if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }

        setRepeatMode(false);
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
                    mediaId = metadata.getDescription().getMediaId();
                    if (RecentUtils.isFavorite(mediaId)) {
                        mIvFavorite.setImageResource(R.mipmap.ic_favorite);
                    }else {
                        mIvFavorite.setImageResource(R.mipmap.ic_favorite_default);
                    }
                    RecentBean recentBean = new RecentBean();
                    recentBean.setMediaId(metadata.getDescription().getMediaId());
                    if (metadata.getDescription().getIconUri() != null) {
                        recentBean.setAlbum(metadata.getDescription().getIconUri().toString());
                    }
                    recentBean.setArtist(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
                    recentBean.setTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                    RecentUtils.addToRecent(recentBean);
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

        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();

        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {

            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }

        if(mCurrentTime != null){
            mCurrentTime.setText(DateUtils.formatElapsedTime(currentPosition / 1000));
        }
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
        mArtist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
        mTitle = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
        if(mSongName != null){
            mSongName.setText(mTitle);
        }
        if(mSongArtist != null){
            mSongArtist.setText(this.mArtist);
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

    /*
    * 展示旋转view
    * */
    private void showRotateImageView(){
        String picture = SPUtils.getInstance().getString("picture", "");
        if(!picture.equals("")){
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.colorWhite)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(getContext())
                    .load(picture)
                    .apply(options)
                    .into(musicCoverView);
        }else {
            musicCoverView.setImageResource(R.mipmap.bg_echelon);
        }

    }

    /*
    * 开始旋转动画
    * */
    private void startAnimation(final View view){
        startRotateAnimator = ObjectAnimator.ofFloat(view, View.ROTATION,0,360);
        startRotateAnimator.setInterpolator(new LinearInterpolator());
        startRotateAnimator.setRepeatCount(Animation.INFINITE);
        startRotateAnimator.setDuration(12000);
        startRotateAnimator.start();
    }

    /*
    * 恢复动画
    * */
    private void restartAnimation(){
        if(startRotateAnimator != null){
            startRotateAnimator.resume();
        }
    }

    /*
    * 结束旋转动画
    * */
    private void stopAnimation(){
        if(startRotateAnimator != null){
            startRotateAnimator.pause();
        }
    }

}
