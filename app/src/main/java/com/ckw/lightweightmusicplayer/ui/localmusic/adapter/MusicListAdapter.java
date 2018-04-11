package com.ckw.lightweightmusicplayer.ui.localmusic.adapter;


import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.view.ViewGroup;

import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalSongViewHolder;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by ckw
 * on 2018/4/8.
 */
public class MusicListAdapter extends RecyclerArrayAdapter<MediaBrowserCompat.MediaItem> {

    public MusicListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return  new LocalSongViewHolder(parent);
    }
}
