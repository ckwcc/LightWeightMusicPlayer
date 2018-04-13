package com.ckw.lightweightmusicplayer.ui.localmusic.viewholder;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.repository.Artist;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class LocalArtistViewHolder extends BaseViewHolder<MediaBrowserCompat.MediaItem> {
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
    public void setData(MediaBrowserCompat.MediaItem data) {
        MediaDescriptionCompat description = data.getDescription();
        String artistName = description.getMediaId();
        String songSize = (String) description.getTitle();
        mSongArtist.setText(artistName);
        mSongNum.setText(String.format(
                mContext.getResources().getString(R.string.song_num),
                Integer.valueOf(songSize)));

    }
}
