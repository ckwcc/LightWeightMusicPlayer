package com.ckw.lightweightmusicplayer.repository;

import java.util.List;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class Album {
    private String Name;
    private List<LocalSong> mAlbumsongs;

    public Album(String name, List<LocalSong> mAlbumsongs) {
        Name = name;
        this.mAlbumsongs = mAlbumsongs;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<LocalSong> getmAlbumsongs() {
        return mAlbumsongs;
    }

    public void setmAlbumsongs(List<LocalSong> mAlbumsongs) {
        this.mAlbumsongs = mAlbumsongs;
    }
}
