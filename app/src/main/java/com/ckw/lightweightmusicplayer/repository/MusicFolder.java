package com.ckw.lightweightmusicplayer.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class MusicFolder {
    private String folderName;
    private List<LocalSong> localSongs;

    public MusicFolder(String folderName) {
        this.folderName = folderName;
    }

    public MusicFolder(String folderName, List<LocalSong> localSongs) {
        this.folderName = folderName;
        this.localSongs = localSongs;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<LocalSong> getLocalSongs() {
        return localSongs;
    }

    public void setLocalSongs(List<LocalSong> localSongs) {
        this.localSongs = localSongs;
    }

    @Override
    public String toString() {
        return "MusicFolder{" +
                "folderName='" + folderName + '\'' +
                ", localSongs=" + localSongs +
                '}';
    }
}
