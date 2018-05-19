package com.ckw.lightweightmusicplayer.ui.favorite

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity

class MyFavoriteActivity : BaseActivity(){

    private var mViewPager: ViewPager? = null
    private var mAdapter: PagerAdapter? = null

    override fun initView(savedInstanceState: Bundle?) {
        val selectedPage = 0
        mViewPager = findViewById(R.id.viewpager)
        mAdapter = PageAdapter(getSupportFragmentManager())

        mViewPager!!.adapter = mAdapter
        mViewPager!!.pageMargin = 40
        mViewPager!!.offscreenPageLimit = 2
        mViewPager!!.currentItem = selectedPage
        mViewPager!!.setPageTransformer(true, FilmPagerTransformer())

    }

    override fun handleBundle(bundle: Bundle) {
    }

    override fun initVariable() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_favorite
    }

    override fun initListener() {
    }

    override fun needToolbar(): Boolean {
        return true
    }

    override fun setToolbar() {
        setToolBarTitle(R.string.play_list)
    }
}