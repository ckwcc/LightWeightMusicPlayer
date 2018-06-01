package com.ckw.lightweightmusicplayer.ui.localalbums

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity

/**
 * Created by ckw
 * on 2018/5/31.
 */
class AlbumDetailActivity: BaseActivity() {
    @BindView(R.id.img_bg)
    lateinit var mImgBg: ImageView
    @BindView(R.id.rv_album_detail)
    lateinit var mRvAlbum: RecyclerView
    @BindView(R.id.tv_title)
    lateinit var mTvAlbum: TextView
    @BindView(R.id.tv_bottom)
    lateinit var mTvArtist: TextView

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun handleBundle(bundle: Bundle) {
    }

    override fun initVariable() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_album_detail
    }

    override fun initListener() {
    }

    override fun needToolbar(): Boolean {
        return false
    }

    override fun setToolbar() {
    }
}