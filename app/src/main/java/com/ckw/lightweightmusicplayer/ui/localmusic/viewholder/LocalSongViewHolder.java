package com.ckw.lightweightmusicplayer.ui.localmusic.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.repository.Song;
import com.ckw.lightweightmusicplayer.utils.MediaUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class LocalSongViewHolder extends BaseViewHolder<Song> {

    private TextView mSongName;
    private TextView mSongArtist;
    private TextView mSongDuration;
    private ImageView mSongImg;

    private Context mContext;

    public LocalSongViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.item_local_song);
        this.mContext = context;
        mSongName  = $(R.id.tv_song);
        mSongArtist= $(R.id.tv_artist);
        mSongImg   = $(R.id.iv_song);
        mSongDuration = $(R.id.tv_duration);
    }

    @Override
    public void setData(Song data) {

        mSongName.setText(data.getTitle());
        mSongArtist.setText(data.getArtist());

        Album album = data.getAlbumObj();
        if (album != null) {
            Glide.with(getContext()).load(album.getAlbumArt()).into(mSongImg);
        }

        mSongDuration.setText(MediaUtils.formatTime(data.getDuration()));



    }
}
