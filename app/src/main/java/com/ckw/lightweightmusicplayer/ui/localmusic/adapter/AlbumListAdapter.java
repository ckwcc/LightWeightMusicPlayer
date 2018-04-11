package com.ckw.lightweightmusicplayer.ui.localmusic.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.ui.localmusic.viewholder.LocalAlbumViewHolder;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by ckw
 * on 2018/4/11.
 */
public class AlbumListAdapter extends RecyclerArrayAdapter<Album> {
    private Context mContext;
    private Activity mActivity;

    public AlbumListAdapter(Context context, Activity activity) {
        super(context);
        mContext = context;
        mActivity = activity;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocalAlbumViewHolder(parent,mContext,mActivity);
    }
}
