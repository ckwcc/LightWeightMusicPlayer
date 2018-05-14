package com.ckw.lightweightmusicplayer.ui;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.repository.RecentBean;

import java.util.List;

/**
 * Created by ckw
 * on 2018/5/10.
 */
public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{

    private List<RecentBean> mData;
    private Context mContext;

    public RecentAdapter(List<RecentBean> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public void setData(List<RecentBean> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recent,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentBean recentBean = mData.get(position);
        holder.tvSongName.setText(recentBean.getTitle());
        holder.tvSongArtist.setText(recentBean.getArtist());
        String album = recentBean.getAlbum();
        if(album != null && !"".equals(album)){
            Glide.with(mContext).load(album)
                    .into(holder.ivAlbum);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivAlbum;
        private TextView tvSongName;
        private TextView tvSongArtist;

        public ViewHolder(View itemView) {
            super(itemView);
            ivAlbum = itemView.findViewById(R.id.iv_album);
            tvSongName = itemView.findViewById(R.id.tv_song_name);
            tvSongArtist = itemView.findViewById(R.id.tv_song_artist);
        }
    }
}
