package com.ckw.lightweightmusicplayer.ui.localmusic.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.util.Pair;
import android.util.Log;
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

public class LocalAlbumViewHolder extends BaseViewHolder<MediaBrowserCompat.MediaItem> {

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
    public void setData(final MediaBrowserCompat.MediaItem  data) {
        super.setData(data);
        final MediaDescriptionCompat description = data.getDescription();
        mAlbumName.setText(description.getMediaId());
        mAlbumArtist.setText(description.getTitle());
        int number = Integer.valueOf(description.getSubtitle().toString());
        if(number > 0){
            mAlbumNumber.setText(
                    String.format(mContext.getResources().getString(R.string.song_num),number));
        }
        if(description.getIconUri() != null){
            Glide.with(mContext).load(description.getIconUri().toString()).into(mAlbumImg);
        }else {
            Glide.with(mContext).load(mContext.getResources().getDrawable(R.mipmap.ic_music_default)).into(mAlbumImg);
        }

        mAlbumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, AlbumActivity.class);
                it.putExtra("albumTitle",description.getMediaId());
                if(description.getIconUri() != null){
                    it.putExtra("albumUri",description.getIconUri().toString());
                }else {
                    it.putExtra("albumUri",mContext.getResources().getDrawable(R.mipmap.ic_music_default).toString());
                }


                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        mActivity,
                        new Pair<View, String>(mAlbumImg,mContext.getString(R.string.translation_thumb)));
                ActivityCompat.startActivity(mContext, it, activityOptions.toBundle());

            }
        });
    }
}
