package com.ckw.lightweightmusicplayer.repository;

import java.util.List;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class Artist {
    private String Name;
    private List<Song> artistsongs;

    public Artist(String name, List<Song> mArtistsongs) {
        Name = name;
        this.artistsongs = mArtistsongs;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<Song> getArtistsongs() {
        return artistsongs;
    }

    public void setArtistsongs(List<Song> mArtistsongs) {
        this.artistsongs = mArtistsongs;
    }
}
