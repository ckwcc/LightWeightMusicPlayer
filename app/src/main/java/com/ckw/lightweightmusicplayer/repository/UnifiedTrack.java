package com.ckw.lightweightmusicplayer.repository;

/**
 * Created by ckw
 * on 2018/3/13.
 */
public class UnifiedTrack {
    private boolean type;                       // true->localTrack         false->streamTrack
    private LocalSong localTrack;
    private Track streamTrack;

    public UnifiedTrack(boolean type, LocalSong localTrack, Track streamTrack) {
        this.type = type;
        this.localTrack = localTrack;
        this.streamTrack = streamTrack;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public LocalSong getLocalTrack() {
        return localTrack;
    }

    public void setLocalTrack(LocalSong localTrack) {
        this.localTrack = localTrack;
    }

    public Track getStreamTrack() {
        return streamTrack;
    }

    public void setStreamTrack(Track streamTrack) {
        this.streamTrack = streamTrack;
    }
}
