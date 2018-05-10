package com.ckw.lightweightmusicplayer.repository;

import android.support.v4.media.MediaBrowserCompat;

import java.util.ArrayList;
import java.util.List;

public  class RecentlyPlayed {
    private List<RecentBean> recentlyPlayed;

    public RecentlyPlayed() {

    }

    public RecentlyPlayed(List<RecentBean> recentlyPlayed) {
        this.recentlyPlayed = recentlyPlayed;
    }

    public List<RecentBean> getRecentlyPlayed() {
        return recentlyPlayed;
    }

    public void setRecentlyPlayed(List<RecentBean> recentlyPlayed) {
        this.recentlyPlayed = recentlyPlayed;
    }

    public void addSong(RecentBean track){
        recentlyPlayed.add(track);
    }
}
