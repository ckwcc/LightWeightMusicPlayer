package com.ckw.lightweightmusicplayer.ui.localmusic.comparators;

import com.ckw.lightweightmusicplayer.repository.Album;

import java.util.Comparator;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class AlbumComparator implements Comparator<Album> {

    @Override
    public int compare(Album lhs, Album rhs) {
        return lhs.getName().toString().compareTo(rhs.getName().toString());
    }
}
