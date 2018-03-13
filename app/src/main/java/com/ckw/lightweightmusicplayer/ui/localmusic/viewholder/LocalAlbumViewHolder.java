package com.ckw.lightweightmusicplayer.ui.localmusic.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class LocalAlbumViewHolder extends BaseViewHolder<Album> {

    private Context mContext;

    private ImageView mAlbumImg;
    private TextView mAlbumName;
    private TextView mAlbumArtist;
    private TextView mAlbumNumber;

    public LocalAlbumViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.item_local_album);
        mContext = context;
        mAlbumImg = $(R.id.iv_album);
        mAlbumName = $(R.id.tv_album_name);
        mAlbumArtist = $(R.id.tv_album_artist);
        mAlbumNumber = $(R.id.tv_album_number);
    }

    @Override
    public void setData(Album data) {
        super.setData(data);
        mAlbumName.setText(data.getAlbum());
        mAlbumArtist.setText(data.getArtist());
        int number = data.getNumSongs();
        if(number > 0){
            mAlbumNumber.setText(
                    String.format(mContext.getResources().getString(R.string.song_num),number));
        }

        if(data.getAlbumArt() != null){
            Glide.with(mContext).load(data.getAlbumArt()).into(mAlbumImg);
        }
    }
}
