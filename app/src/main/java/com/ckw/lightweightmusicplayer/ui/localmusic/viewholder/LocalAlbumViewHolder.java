package com.ckw.lightweightmusicplayer.ui.localmusic.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.ui.localmusic.activities.AlbumActivity;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class LocalAlbumViewHolder extends BaseViewHolder<Album> {

    private Context mContext;
    private Activity mActivity;

    private ImageView mAlbumImg;
    private TextView mAlbumName;
    private TextView mAlbumArtist;
    private TextView mAlbumNumber;

    public LocalAlbumViewHolder(ViewGroup parent, Context context,Activity activity) {
        super(parent, R.layout.item_local_album);
        mContext = context;
        mActivity = activity;
        mAlbumImg = $(R.id.iv_album);
        mAlbumName = $(R.id.tv_album_name);
        mAlbumArtist = $(R.id.tv_album_artist);
        mAlbumNumber = $(R.id.tv_album_number);
    }

    @Override
    public void setData(final Album data) {
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

        mAlbumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, AlbumActivity.class);
                it.putExtra("album", data);

                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity)mContext, mAlbumImg, mContext.getString(R.string.translation_thumb));

                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        mActivity,
                        new Pair<View, String>(mAlbumImg,mContext.getString(R.string.translation_thumb)));
                ActivityCompat.startActivity(mContext, it, activityOptions.toBundle());

            }
        });
    }
}
