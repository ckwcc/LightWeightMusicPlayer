package com.ckw.lightweightmusicplayer.ui.localalbums

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import butterknife.BindView
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity
import com.ckw.lightweightmusicplayer.ui.main.ItemClickListener
import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper.MEDIA_ID_ALBUM
import com.dingmouren.layoutmanagergroup.skidright.SkidRightLayoutManager

/**
 * Created by ckw
 * on 2018/5/31.
 */
class LocalAlbumsActivity : BaseActivity(), AlbumClickListener {

    @BindView(R.id.recycler_view)
    lateinit var mRecyclerView: RecyclerView

    private var mLayoutManager: SkidRightLayoutManager? = null
    private var mList: MutableList<MediaBrowserCompat.MediaItem>? = null
    private var mAdapter: LocalAlbumsAdapter? = null

    override fun onMediaBrowserConnected() {
        super.onMediaBrowserConnected()
        onConnected()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mLayoutManager = SkidRightLayoutManager(1.5f,0.85f)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = LocalAlbumsAdapter(mList!!,this,this)
        mRecyclerView.adapter = mAdapter
    }

    override fun handleBundle(bundle: Bundle) {
    }

    override fun initVariable() {
        mList = mutableListOf()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_local_albums
    }

    override fun initListener() {
    }

    override fun needToolbar(): Boolean {
        return false
    }

    override fun setToolbar() {
    }

    override fun setOnItemClick(position: Int, viewHolder: LocalAlbumsAdapter.ViewHolder) {
        val mediaItem = mList!![position]
        val intent = Intent(this, AlbumDetailActivity::class.java)
        intent.putExtra("img", mediaItem.description.iconUri.toString())
        intent.putExtra("album", mediaItem.description.mediaId)
        intent.putExtra("artist",mediaItem.description.title)
        val p1 = Pair.create(viewHolder!!.imgBg as View, "img_view_1")
        val p2 = Pair.create(viewHolder!!.tvTitle as View, "title_1")
        val p3 = Pair.create(viewHolder!!.tvBottom as View, "tv_bottom")
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3)
        startActivity(intent, options.toBundle())
    }

    private fun onConnected() {
        val mediaId = MEDIA_ID_ALBUM
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


}