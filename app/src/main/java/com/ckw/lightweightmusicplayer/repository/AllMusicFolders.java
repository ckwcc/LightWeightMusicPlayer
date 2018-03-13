package com.ckw.lightweightmusicplayer.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class AllMusicFolders {
    List<MusicFolder> musicFolders;

    public AllMusicFolders() {
        musicFolders = new ArrayList<>();
    }

    public List<MusicFolder> getMusicFolders() {
        return musicFolders;
    }

    public void setMusicFolders(List<MusicFolder> musicFolders) {
        this.musicFolders = musicFolders;
    }
}
