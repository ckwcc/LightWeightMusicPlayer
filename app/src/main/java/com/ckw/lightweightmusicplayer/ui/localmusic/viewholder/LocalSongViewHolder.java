package com.ckw.lightweightmusicplayer.ui.localmusic.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
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

public class LocalSongViewHolder extends BaseViewHolder<MediaBrowserCompat.MediaItem > {

    private TextView mSongName;
    private TextView mSongArtist;
    private TextView mSongDuration;
    private ImageView mSongImg;


    public LocalSongViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_local_song);
        mSongName  = $(R.id.tv_song);
        mSongArtist= $(R.id.tv_artist);
        mSongImg   = $(R.id.iv_song);
        mSongDuration = $(R.id.tv_duration);
    }

    @Override
    public void setData(MediaBrowserCompat.MediaItem  data) {
        //MediaItem中不支持获取音乐时长，所以时长在这里就不显示了
        MediaDescriptionCompat description = data.getDescription();

        mSongName.setText(description.getTitle());
        mSongArtist.setText(description.getSubtitle());

        Uri iconUri = description.getIconUri();
        if (iconUri != null) {
            Glide.with(getContext()).load(iconUri.toString())
                    .into(mSongImg);
        }


    }
}
