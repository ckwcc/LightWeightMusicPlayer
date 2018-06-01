package com.ckw.lightweightmusicplayer.ui.localalbums

import android.graphics.Color
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity
import com.ckw.lightweightmusicplayer.ui.localmusic.adapter.MusicListAdapter
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity
import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper
import com.jude.easyrecyclerview.EasyRecyclerView
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter
import com.jude.easyrecyclerview.decoration.DividerDecoration

/**
 * Created by ckw
 * on 2018/5/31.
 */
class AlbumDetailActivity: BaseActivity() {
    @BindView(R.id.img_bg)
    lateinit var mImgBg: ImageView
    @BindView(R.id.rv_album_detail)
    lateinit var mRvAlbum: EasyRecyclerView
    @BindView(R.id.tv_title)
    lateinit var mTvAlbum: TextView
    @BindView(R.id.tv_bottom)
    lateinit var mTvArtist: TextView

    private var mImgPath: String? = null
    private var mAlbumName: String? = null
    private var mArtistName: String? = null

    private var mAdapter: RecyclerArrayAdapter<MediaBrowserCompat.MediaItem>? = null
    private var mList: ArrayList<MediaBrowserCompat.MediaItem>? = null

    override fun initView(savedInstanceState: Bundle?) {
        if (mImgPath != null && "" != mImgPath && "null" != mImgPath){
            Glide.with(this).load(mImgPath).into(mImgBg)
        }else{
            mImgBg.setImageResource(R.mipmap.bg_echelon)
        }
        mTvAlbum.text = mAlbumName
        mTvArtist.text = mArtistName

        initRecyclerView()
    }

    override fun handleBundle(bundle: Bundle) {
        mImgPath = bundle.get("img") as String
        mAlbumName = bundle.get("album") as String
        mArtistName = bundle.get("artist") as String

    }

    override fun initVariable() {
        mList = ArrayList()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_album_detail
    }

    override fun initListener() {
        mAdapter!!.setOnItemClickListener {
            if(it == -1){
                return@setOnItemClickListener
            }
            val mediaItem = mList!![it]
            val iconUri = mediaItem.description.iconUri
            val bundle = Bundle()
            bundle.putString("musicId",mediaItem.mediaId)
            if(iconUri != null){
                bundle.putString("iconUri",iconUri.toString())
            }
            bundle.putBoolean("play",true)
            ActivityUtils.startActivity(bundle,MusicPlayActivity::class.java)
        }
    }

    override fun needToolbar(): Boolean {
        return false
    }

    override fun setToolbar() {
    }

    override fun onMediaBrowserConnected() {
        super.onMediaBrowserConnected()
        onConnected()
    }

    fun onConnected() {
        val mediaId = MediaIdHelper.MEDIA_ID_ALBUM_DETAIL + "&&" + mAlbumName
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
            mAdapter!!.clear()
            mAdapter!!.addAll(children)
        }
    }

    private fun initRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(this)
        mRvAlbum.setLayoutManager(linearLayoutManager)
        val itemDecoration = DividerDecoration(Color.GRAY, SizeUtils.dp2px(1f), SizeUtils.dp2px(8.0f),0)
        itemDecoration.setDrawLastItem(false)
        mRvAlbum.addItemDecoration(itemDecoration)

        mAdapter = MusicListAdapter(this)
        mRvAlbum.adapter = mAdapter
    }
}