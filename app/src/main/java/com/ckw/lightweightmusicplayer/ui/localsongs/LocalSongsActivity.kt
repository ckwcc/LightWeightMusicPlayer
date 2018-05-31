package com.ckw.lightweightmusicplayer.ui.localsongs

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.media.session.MediaSessionCompat

import com.blankj.utilcode.util.FragmentUtils
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity

import javax.inject.Inject

import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper.MEDIA_ID_NORMAL

/**
 * Created by ckw
 * on 2018/5/31.
 */
class LocalSongsActivity : BaseActivity() {
    @Inject
    lateinit var localSongsFragment: LocalSongsFragment

    private var manager: FragmentManager? = null

    override fun onMediaBrowserConnected() {
        super.onMediaBrowserConnected()
        localSongsFragment.onConnected(mediaBrowser)
    }

    override fun initView(savedInstanceState: Bundle?) {
        FragmentUtils.add(manager!!, localSongsFragment, R.id.constraint_layout_songs)
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
