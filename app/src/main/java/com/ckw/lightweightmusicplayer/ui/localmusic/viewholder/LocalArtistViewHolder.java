package com.ckw.lightweightmusicplayer.ui.localmusic.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.repository.Artist;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class LocalArtistViewHolder extends BaseViewHolder<Artist> {
    private TextView mSongArtist;
    private TextView mSongNum;

    private Context mContext;

    public LocalArtistViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.item_local_artist);
        this.mContext = context;
        mSongArtist  = $(R.id.tv_song);
        mSongNum= $(R.id.tv_artist);
    }

    @Override
    public void setData(Artist data) {
        mSongArtist.setText(data.getName());
        mSongNum.setText(String.format(
                mContext.getResources().getString(R.string.song_num),
                data.getArtistsongs().size()));

    }
}
