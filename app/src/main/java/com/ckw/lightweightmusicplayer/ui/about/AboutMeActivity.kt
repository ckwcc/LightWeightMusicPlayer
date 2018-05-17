package com.ckw.lightweightmusicplayer.ui.about

import android.os.Bundle
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity

/**
 * Created by ckw
 * on 2018/5/17.
 */
class AboutMeActivity : BaseActivity() {

    override fun handleBundle(bundle: Bundle) {
    }

    override fun initVariable() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_about_me
    }

    override fun initListener() {

    }

    override fun needToolbar(): Boolean {
        return true
    }

    override fun setToolbar() {
        setToolBarTitle(R.string.drawer_item_about)
    }

    override fun initView(savedInstanceState: Bundle?) {
    }
}