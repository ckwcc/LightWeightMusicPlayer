package com.ckw.lightweightmusicplayer.ui.localmusic.adapter;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.view.ViewGroup;

import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalArtistViewHolder;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by ckw
 * on 2018/4/13.
 */
public class ArtistListAdapter extends RecyclerArrayAdapter<MediaBrowserCompat.MediaItem> {

    private Context mContext;

    public ArtistListAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocalArtistViewHolder(parent,mContext);
    }
}
