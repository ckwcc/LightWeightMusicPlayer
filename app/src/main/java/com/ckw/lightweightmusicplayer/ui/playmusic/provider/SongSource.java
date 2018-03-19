package com.ckw.lightweightmusicplayer.ui.playmusic.provider;

import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ckw
 * on 2018/3/19.
 */

public interface SongSource {
    String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    Iterator<MediaMetadataCompat> iterator();
    ArrayList<MediaMetadataCompat> getLocalList();
}
