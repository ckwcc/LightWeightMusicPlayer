package com.ckw.lightweightmusicplayer.repository;

import java.util.List;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class Artist {
    private String Name;
    private List<LocalSong> mArtistsongs;

    public Artist(String name, List<LocalSong> mArtistsongs) {
        Name = name;
        this.mArtistsongs = mArtistsongs;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<LocalSong> getmArtistsongs() {
        return mArtistsongs;
    }

    public void setmArtistsongs(List<LocalSong> mArtistsongs) {
        this.mArtistsongs = mArtistsongs;
    }
}
