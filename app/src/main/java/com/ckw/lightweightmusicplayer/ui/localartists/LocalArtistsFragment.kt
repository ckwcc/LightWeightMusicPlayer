package com.ckw.lightweightmusicplayer.ui.localartists

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseFragment
import com.ckw.lightweightmusicplayer.ui.localmusic.activities.ArtistActivity
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity
import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper
import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper.MEDIA_ID_ARTIST
import com.ckw.lightweightmusicplayer.utils.KLogUtil
import com.dingmouren.layoutmanagergroup.slide.ItemConfig
import com.dingmouren.layoutmanagergroup.slide.ItemTouchHelperCallback
import com.dingmouren.layoutmanagergroup.slide.OnSlideListener
import com.dingmouren.layoutmanagergroup.slide.SlideLayoutManager
import javax.inject.Inject

/**
 * Created by ckw
 * on 2018/6/1.
 */
class LocalArtistsFragment @Inject constructor(): BaseFragment(){

    @BindView(R.id.recycler_view_artists)
    lateinit var mRecyclerView: RecyclerView

    private var mAdapter: LocalArtistAdapter? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var mItemTouchHelperCallback: ItemTouchHelperCallback<MediaBrowserCompat.MediaItem>? = null
    private var mList: ArrayList<MediaBrowserCompat.MediaItem>? = null
    private var mCopyList: ArrayList<MediaBrowserCompat.MediaItem>? = null

    override fun getLayoutResID(): Int {
        return R.layout.fragment_local_artists
    }

    override fun initVariables() {
        mList = ArrayList()
        mCopyList = ArrayList()
    }

    override fun handleBundle(bundle: Bundle?) {
    }

    override fun operateViews(view: View?) {
        mAdapter = LocalArtistAdapter()
        mRecyclerView.adapter = mAdapter
        mItemTouchHelperCallback = ItemTouchHelperCallback(mRecyclerView.adapter, mList!!)
        mItemTouchHelper = ItemTouchHelper(mItemTouchHelperCallback)
        val slideLayoutManager = SlideLayoutManager(mRecyclerView,mItemTouchHelper!!)
        mItemTouchHelper!!.attachToRecyclerView(mRecyclerView)
        mRecyclerView.layoutManager = slideLayoutManager
    }

    override fun initListener() {
        mItemTouchHelperCallback!!.setOnSlideListener(object : OnSlideListener<MediaBrowserCompat.MediaItem>{
            override fun onSliding(p0: RecyclerView.ViewHolder?, p1: Float, p2: Int) {
            }

            override fun onClear() {
                mList!!.addAll(mCopyList!!)
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onSlided(p0: RecyclerView.ViewHolder?, p1: MediaBrowserCompat.MediaItem?, p2: Int) {
            }
        }
        )
    }

    override fun initPresenter() {
    }

    fun onConnected(mediaBrowser: MediaBrowserCompat) {
        val mediaId = MEDIA_ID_ARTIST
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
            mCopyList!!.clear()
            mCopyList!!.addAll(children)
            mAdapter!!.notifyDataSetChanged()

        }
    }

    internal inner class LocalArtistAdapter : RecyclerView.Adapter<LocalArtistAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slide, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val mediaItem = mList!![position]
            val artistName = mediaItem.description.mediaId
            val songs = mediaItem.description.title as String
            holder.artistName.text = artistName
            holder.songNum.text = String.format(resources.getString(R.string.song_num),Integer.valueOf(songs))

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ArtistActivity::class.java)
                intent.putExtra("artistName", artistName)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return mList!!.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var artistName: TextView
            internal var songNum: TextView
            init {
                artistName = itemView.findViewById(R.id.tv_artist_name)
                songNum = itemView.findViewById(R.id.tv_song_num)

            }
        }
    }
}