package com.ckw.lightweightmusicplayer.ui.localmusic.comparators;

import com.ckw.lightweightmusicplayer.repository.LocalSong;

import java.util.Comparator;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class LocalMusicComparator implements Comparator<LocalSong> {

    @Override
    public int compare(LocalSong lhs, LocalSong rhs) {
        return lhs.getTitle().toString().compareTo(rhs.getTitle().toString());
    }
}
