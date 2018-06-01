package com.ckw.lightweightmusicplayer.ui.localalbums

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.ui.main.ItemClickListener

/**
 * Created by ckw
 * on 2018/5/31.
 */
class LocalAlbumsAdapter() : RecyclerView.Adapter<LocalAlbumsAdapter.ViewHolder>(){
    private var mList: MutableList<MediaBrowserCompat.MediaItem>?= null
    private var mContext: Context? = null
    private var listener: AlbumClickListener? = null
    constructor(list: MutableList<MediaBrowserCompat.MediaItem>,context: Context,listener: AlbumClickListener) : this() {
        mList = list
        mContext = context
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_skid_right,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaItem = mList!![position]

        holder.tvBottom!!.text = mediaItem.description.title
        holder.tvTitle!!.text = mediaItem.description.mediaId
        if (mediaItem.description.iconUri != null && mediaItem.description.iconUri!!.toString() != "") {
            Glide.with(mContext!!).load(mediaItem.description.iconUri!!.toString())
                    .into(holder.imgBg!!)
        } else {
            holder.imgBg!!.setImageResource(R.mipmap.bg_echelon)
        }
        holder.imgBg!!.setOnClickListener {
            listener!!.setOnItemClick(position,holder)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        internal var imgBg: ImageView? = null
        internal var tvTitle: TextView? = null
        internal var tvBottom: TextView? = null
        init {
            imgBg = itemView.findViewById(R.id.img_bg)
            tvTitle = itemView.findViewById(R.id.tv_title)
            tvBottom = itemView.findViewById(R.id.tv_bottom)
        }
    }
}