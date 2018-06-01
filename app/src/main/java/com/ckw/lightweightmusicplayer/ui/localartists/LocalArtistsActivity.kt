package com.ckw.lightweightmusicplayer.ui.localartists

import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.blankj.utilcode.util.FragmentUtils
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity
import javax.inject.Inject

/**
 * Created by ckw
 * on 2018/6/1.
 */
class LocalArtistsActivity : BaseActivity(){
    @Inject
    lateinit var localArtistsFragment: LocalArtistsFragment

    private var manager: FragmentManager? = null

    override fun onMediaBrowserConnected() {
        super.onMediaBrowserConnected()
        localArtistsFragment.onConnected(mediaBrowser)
    }

    override fun initView(savedInstanceState: Bundle?) {
        FragmentUtils.add(manager!!,localArtistsFragment,R.id.constraint_layout_songs)
    }

    override fun handleBundle(bundle: Bundle) {
    }

    override fun initVariable() {
        manager = supportFragmentManager
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_local_songs
    }

    override fun initListener() {
    }

    override fun needToolbar(): Boolean {
        return false
    }

    override fun setToolbar() {
    }
}