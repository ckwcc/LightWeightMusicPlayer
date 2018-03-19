/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ckw.lightweightmusicplayer.ui.playmusic.playback;


import android.support.v4.media.session.MediaSessionCompat;

import com.ckw.lightweightmusicplayer.repository.Song;


/**
 * Interface representing either Local or Remote Playback.代表本地或远程 播放的接口。
 * The {@link com.ckw.lightweightmusicplayer.ui.playmusic.service.MusicService} works directly with an instance of the Playback object to make the various calls such as
 * play, pause etc.
 * 音乐服务 直接与播放对象的一个实例一起工作，以进行各种调用，如play、pause等。
 */
public interface Playback {
    /**
     * Start/setup the playback.
     * Resources/listeners would be allocated by implementations.
     */
    void start();

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     * 停止播放，所有的资源都可以通过这里的实现来分配。
     * @param notifyListeners if true and a callback has been set by setCallback,
     *                        callback.onPlaybackStatusChanged will be called after changing
     *                        the state.
     *                        如果返回true，并且callback接口已经通过setCallback方法设置了，
     *                        callback的onPlaybackStatusChanged方法将会在state状态改变后被调用
     */
    void stop(boolean notifyListeners);

    /**
     * Set the latest playback state as determined by the caller.
     * 设置最新的确定的播放状态
     */
    void setState(int state);

    /**
     * Get the current {@link android.media.session.PlaybackState#getState()}
     */
    int getState();

    /**
     * @return boolean that indicates that this is ready to be used.
     * 表明是否已经准备好被使用，返回boolean
     */
    boolean isConnected();

    /**
     * @return boolean indicating whether the player is playing or is supposed to be
     * playing when we gain audio focus.
     * 当我们获得音频焦点的时候，返回一个boolean值来表明 播放器是否是正在播放或者被认为是正在播放
     */
    boolean isPlaying();

    /**
     * @return pos if currently playing an item
     * 如果当前正在播放一个项目，返回它的position
     */
    long getCurrentStreamPosition();

    /**
     * Queries the underlying stream and update the internal last known stream position.
     * 查询底层流并更新内部最后已知的流位置。
     */
    void updateLastKnownStreamPosition();

    void play(MediaSessionCompat.QueueItem item);

    void pause();

    void seekTo(long position);

    void setCurrentMediaId(String mediaId);

    String getCurrentMediaId();

    interface Callback {
        /**
         * On current music completed.
         * 当前音乐完成
         */
        void onCompletion();
        /**
         * on Playback status changed
         * Implementations can use this callback to update
         * playback state on the media sessions.
         * 在播放状态改变的时候，
         * 实现它，可以使用这个回调来更新媒体会话中的播放状态。
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error to be added to the PlaybackState
         */
        void onError(String error);

        /**
         * @param mediaId being currently played
         *                当前播放的id
         */
        void setCurrentMediaId(String mediaId);
    }

    void setCallback(Callback callback);
}
