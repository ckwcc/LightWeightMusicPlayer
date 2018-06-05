package com.ckw.lightweightmusicplayer.ui.playmusic.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.ckw.lightweightmusicplayer.ui.playmusic.provider.MusicProvider;
import com.ckw.lightweightmusicplayer.ui.playmusic.provider.SongSource;
import com.ckw.lightweightmusicplayer.ui.playmusic.service.MusicService;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC;
import static com.google.android.exoplayer2.C.USAGE_MEDIA;


/**
 * Created by ckw
 * on 2018/3/19.
 */

public final class LocalPlayback implements Playback {

    //当我们失去音频焦点时，我们设置媒体播放器的音量，允许减少音量而不是停止播放。
    public static final float VOLUME_DUCK = 0.2f;
    //当我们拥有音频焦点的时候，我们设置的音量
    public static final float VOLUME_NORMAL = 1.0f;

    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;

    private final Context mContext;
    private boolean mPlayOnFocusGain;//是否有音频焦点
    private Callback mCallback;//PlaybackManager实现了它
    private final MusicProvider mMusicProvider;
    private String mCurrentMediaId;//title + artist组成的hashcode值

    private boolean mAudioNoisyReceiverRegistered;//耳机插拔监听广播是否注册
    private int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;//音频焦点状态值
    private final AudioManager mAudioManager;//控制音量的管理器
    private SimpleExoPlayer mExoPlayer;//类似于MediaPlayer
    private final ExoPlayerEventListener mEventListener = new ExoPlayerEventListener();

    // Whether to return STATE_NONE or STATE_STOPPED when mExoPlayer is null;
    private boolean mExoPlayerNullIsStopped =  false;

    public LocalPlayback(Context context, MusicProvider musicProvider) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mMusicProvider = musicProvider;

        this.mAudioManager =
                (AudioManager) applicationContext.getSystemService(Context.AUDIO_SERVICE);

    }

    private void giveUpAudioFocus() {
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private final IntentFilter mAudioNoisyIntentFilter =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    /*
  * 监听耳机的插拔
  * */
    private final BroadcastReceiver mAudioNoisyReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                        if (isPlaying()) {
                            Intent i = new Intent(context, MusicService.class);
                            i.setAction(MusicService.ACTION_CMD);
                            i.putExtra(MusicService.CMD_NAME, MusicService.CMD_PAUSE);
                            mContext.startService(i);
                        }
                    }
                }
            };


    private void registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mContext.registerReceiver(mAudioNoisyReceiver, mAudioNoisyIntentFilter);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            mContext.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    /*
    * 处理音频焦点，处理播放器的暂停与恢复
    * */
    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mCurrentAudioFocusState = AUDIO_FOCUSED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                            //音频焦点丢失了，但也有可能隐藏(例如:静音播放)
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            // Lost audio focus, but will gain it back (shortly), so note whether
                            // playback should resume  所以请注意播放是否应该恢复
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost audio focus, probably "permanently" 丢失的音频焦点，可能是“永久的”
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    if (mExoPlayer != null) {
                        // Update the player state based on the change
                        configurePlayerState();
                    }

                }
            };


    /*
    * 释放资源
    * */
    private void releaseResources(boolean releasePlayer) {

        // Stops and releases player (if requested and available).
        if (releasePlayer && mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer.removeListener(mEventListener);
            mExoPlayer = null;
            mExoPlayerNullIsStopped = true;
            mPlayOnFocusGain = false;
        }

    }


    @Override
    public void start() {

    }

    @Override
    public void stop(boolean notifyListeners) {
        giveUpAudioFocus();
        unregisterAudioNoisyReceiver();
        releaseResources(true);
    }

    @Override
    public void setState(int state) {

    }

    @Override
    public int getState() {
        if (mExoPlayer == null) {
            return mExoPlayerNullIsStopped
                    ? PlaybackStateCompat.STATE_STOPPED
                    : PlaybackStateCompat.STATE_NONE;
        }

        switch (mExoPlayer.getPlaybackState()) {
            case ExoPlayer.STATE_IDLE:
                return PlaybackStateCompat.STATE_PAUSED;
            case ExoPlayer.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case ExoPlayer.STATE_READY:
                return mExoPlayer.getPlayWhenReady()
                        ? PlaybackStateCompat.STATE_PLAYING
                        : PlaybackStateCompat.STATE_PAUSED;
            case ExoPlayer.STATE_ENDED:
                return PlaybackStateCompat.STATE_PAUSED;
            default:
                return PlaybackStateCompat.STATE_NONE;
        }


    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        return mPlayOnFocusGain || (mExoPlayer != null && mExoPlayer.getPlayWhenReady());
    }

    @Override
    public long getCurrentStreamPosition() {
        return mExoPlayer != null ? mExoPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void updateLastKnownStreamPosition() {
        // Nothing to do. Position maintained by ExoPlayer.
    }

    @Override
    public void play(MediaSessionCompat.QueueItem item,boolean isFinish) {

        mPlayOnFocusGain = true;
        tryToGetAudioFocus();
        registerAudioNoisyReceiver();
        String mediaId = item.getDescription().getMediaId();
        if(!isFinish){
            boolean mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaId);
            if (mediaHasChanged) {
                mCurrentMediaId = mediaId;
            }

            if (mediaHasChanged ||mExoPlayer == null) {
                releaseResources(false); // release everything except the player
                MediaMetadataCompat track =
                        mMusicProvider.getMusic(item.getDescription().getMediaId());

                String source = track.getString(SongSource.CUSTOM_METADATA_TRACK_SOURCE);
                if (source != null) {
                    source = source.replaceAll(" ", "%20"); // Escape spaces for URLs
                }

                if (mExoPlayer == null) {
                    mExoPlayer =
                            ExoPlayerFactory.newSimpleInstance(
                                    mContext, new DefaultTrackSelector(), new DefaultLoadControl());
                    mExoPlayer.addListener(mEventListener);
                }

                final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(CONTENT_TYPE_MUSIC)
                        .setUsage(USAGE_MEDIA)
                        .build();
                mExoPlayer.setAudioAttributes(audioAttributes);

                DataSource.Factory dataSourceFactory =
                        new DefaultDataSourceFactory(
                                mContext, Util.getUserAgent(mContext, "cnuo"), null);
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource =
                        new ExtractorMediaSource(
                                Uri.parse(source), dataSourceFactory, extractorsFactory, null, null);
                mExoPlayer.prepare(mediaSource);
            }
        }else {
            mCurrentMediaId = mediaId;
            releaseResources(false); // release everything except the player
            MediaMetadataCompat track =
                    mMusicProvider.getMusic(item.getDescription().getMediaId());

            String source = track.getString(SongSource.CUSTOM_METADATA_TRACK_SOURCE);
            if (source != null) {
                source = source.replaceAll(" ", "%20"); // Escape spaces for URLs
            }

            if (mExoPlayer == null) {
                mExoPlayer =
                        ExoPlayerFactory.newSimpleInstance(
                                mContext, new DefaultTrackSelector(), new DefaultLoadControl());
            }
            mExoPlayer.addListener(mEventListener);

            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build();
            mExoPlayer.setAudioAttributes(audioAttributes);

            DataSource.Factory dataSourceFactory =
                    new DefaultDataSourceFactory(
                            mContext, Util.getUserAgent(mContext, "uamp"), null);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource =
                    new ExtractorMediaSource(
                            Uri.parse(source), dataSourceFactory, extractorsFactory, null, null);
            mExoPlayer.prepare(mediaSource);
        }


        configurePlayerState();
    }

    @Override
    public void pause() {
        // Pause player and cancel the 'foreground service' state.
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
        // While paused, retain the player instance, but give up audio focus.
        releaseResources(false);
        unregisterAudioNoisyReceiver();
    }

    @Override
    public void seekTo(long position) {
        if (mExoPlayer != null) {
            registerAudioNoisyReceiver();
            mExoPlayer.seekTo(position);
        }
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        this.mCurrentMediaId = mediaId;
    }

    @Override
    public String getCurrentMediaId() {
        return mCurrentMediaId;
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    /*
    * 获取音频焦点
    * */
    private void tryToGetAudioFocus() {
        int result =
                mAudioManager.requestAudioFocus(
                        mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        } else {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    /**
     * Reconfigures the player according to audio focus settings and starts/restarts it. This method
     * starts/restarts the ExoPlayer instance respecting the current audio focus state. 根据当前的音频焦点状态来决定
     * So if we have focus, it will play normally; if we don't have focus, it will either leave the player
     * paused or set it to a low volume, depending on what is permitted by the current focus
     * settings.
     */
    private void configurePlayerState() {
        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pause();
        } else {
            registerAudioNoisyReceiver();

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                // We're permitted to play, but only if we 'duck', ie: play softly
                mExoPlayer.setVolume(VOLUME_DUCK);
            } else {
                mExoPlayer.setVolume(VOLUME_NORMAL);
            }

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                mExoPlayer.setPlayWhenReady(true);
                mPlayOnFocusGain = false;
            }
        }
    }


    /*
    * 播放器事件监听
    * */
    private final class ExoPlayerEventListener implements ExoPlayer.EventListener {


        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(
                TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Nothing to do.
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Nothing to do.
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                case ExoPlayer.STATE_BUFFERING:
                case ExoPlayer.STATE_READY:
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(getState());
                    }
                    break;
                case ExoPlayer.STATE_ENDED:
                    // The media player finished playing the current song.
                    if (mCallback != null) {
                        mCallback.onCompletion();
                    }
                    break;

            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            final String what;
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    what = error.getSourceException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    what = error.getRendererException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    what = error.getUnexpectedException().getMessage();
                    break;
                default:
                    what = "Unknown: " + error;
            }

            if (mCallback != null) {
                mCallback.onError("ExoPlayer error " + what);
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }


        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Nothing to do.
        }

        @Override
        public void onSeekProcessed() {

        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }
    }
}
