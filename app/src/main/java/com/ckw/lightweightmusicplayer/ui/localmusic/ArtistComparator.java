package com.ckw.lightweightmusicplayer.ui.localmusic;

import com.ckw.lightweightmusicplayer.repository.Artist;

import java.util.Comparator;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class ArtistComparator implements Comparator<Artist> {

    @Override
    public int compare(Artist lhs, Artist rhs) {
        return lhs.getName().toString().compareTo(rhs.getName().toString());
    }
}
