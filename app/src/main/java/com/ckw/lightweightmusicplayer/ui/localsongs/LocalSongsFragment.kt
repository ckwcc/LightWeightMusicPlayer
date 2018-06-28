package com.ckw.lightweightmusicplayer.ui.localsongs

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.blankj.utilcode.util.ActivityUtils
import com.bumptech.glide.Glide
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseFragment
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity
import com.dingmouren.layoutmanagergroup.echelon.EchelonLayoutManager

import java.util.ArrayList

import javax.inject.Inject

import butterknife.BindView
import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper


/**
 * Created by ckw
 * on 2018/5/31.
 */
class LocalSongsFragment @Inject constructor() : BaseFragment() {


     @BindView(R.id.recycler_view)
     lateinit var mRecyclerView: RecyclerView

    private var mList: MutableList<MediaBrowserCompat.MediaItem>? = null
    private var mAdapter: LocalSongAdapter? = null

    override fun getLayoutResID(): Int {
        return R.layout.fragment_local_song
    }

    override fun initVariables() {
        mList = ArrayList()
    }

    override fun handleBundle(bundle: Bundle) {

    }

    override fun operateViews(view: View) {
        val echelonLayoutManager = EchelonLayoutManager(context)
        mRecyclerView.layoutManager = echelonLayoutManager
        mAdapter = LocalSongAdapter()
        mRecyclerView.adapter = mAdapter
    }

    override fun initListener() {

    }

    override fun initPresenter() {

    }

    fun onConnected(mediaBrowser: MediaBrowserCompat) {
        val mediaId = MediaIdHelper.MEDIA_ID_NORMAL
        mediaBrowser.unsubscribe(mediaId)
        mediaBrowser.subscribe(mediaId, mSubscriptionCallback)
    }

    /*
    * 浏览器订阅的接口，数据的回调
    * */
    private val mSubscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
            super.onChildrenLoaded(parentId, children)

            mList!!.clear()
            mList!!.addAll(children)
            mAdapter!!.notifyDataSetChanged()

        }
    }

    internal inner class LocalSongAdapter : RecyclerView.Adapter<LocalSongAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_echelon, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val mediaItem = mList!![position]

            holder.nickName.text = mediaItem.description.subtitle
            holder.desc.text = mediaItem.description.title
            if (mediaItem.description.iconUri != null) {
                Glide.with(context!!).load(mediaItem.description.iconUri!!.toString())
                        .into(holder.bg)
            } else {
                holder.bg.setImageResource(R.mipmap.ic_music_default)
            }
            holder.bg.setOnClickListener {
                val iconUri = mediaItem.description.iconUri

                val bundle = Bundle()
                bundle.putString("musicId", mediaItem.mediaId)
                if (iconUri != null) {
                    bundle.putString("iconUri", iconUri.toString())
                }
                bundle.putBoolean("play", true)
                ActivityUtils.startActivity(bundle, MusicPlayActivity::class.java)
            }
        }

        override fun getItemCount(): Int {
            return mList!!.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var icon: ImageView
            internal var bg: ImageView
            internal var nickName: TextView
            internal var desc: TextView

            init {
                icon = itemView.findViewById(R.id.img_icon)
                bg = itemView.findViewById(R.id.img_bg)
                nickName = itemView.findViewById(R.id.tv_nickname)
                desc = itemView.findViewById(R.id.tv_desc)

            }
        }
    }
}
