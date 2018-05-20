package com.ckw.lightweightmusicplayer.ui.favorite

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.blankj.utilcode.util.SPUtils
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity
import com.ckw.lightweightmusicplayer.repository.RecentBean
import com.ckw.lightweightmusicplayer.repository.RecentlyPlayed
import com.google.gson.Gson

class MyFavoriteActivity : BaseActivity(){

    private var mViewPager: ViewPager? = null
    private var mAdapter: PagesAdapter? = null
    private var mList: ArrayList<RecentBean>? = null
    private var mFragments: ArrayList<Fragment>? = null

    override fun initView(savedInstanceState: Bundle?) {
        getMyFavoriteData()
        initData()
        val selectedPage = 0
        mViewPager = findViewById(R.id.viewpager)
        mAdapter = PagesAdapter(supportFragmentManager,mFragments!!)

        mViewPager!!.adapter = mAdapter
        mViewPager!!.pageMargin = 40
        mViewPager!!.offscreenPageLimit = 2
        mViewPager!!.currentItem = selectedPage
        mViewPager!!.setPageTransformer(true, FilmPagerTransformer())

    }

    override fun handleBundle(bundle: Bundle) {
    }

    override fun initVariable() {
        mList = ArrayList()
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

    fun initData(): Unit {
        mFragments = ArrayList()
        for (i in 0 until mList!!.size) {
            val fragment: PlaceholderFragment = PlaceholderFragment()
            val bundle = Bundle()
            bundle.putInt("position",i)
            bundle.putString("title",mList!!.get(i).title)
            bundle.putString("artist",mList!!.get(i).artist)
            bundle.putString("album",mList!!.get(i).album)
            fragment.arguments = bundle
            mFragments!!.add(fragment)
        }
    }

    fun getMyFavoriteData(): Unit {
        val favorite = SPUtils.getInstance().getString("favorite")
        val gsonFavorite = Gson()

        var favoritePlayed = gsonFavorite.fromJson(favorite,RecentlyPlayed::class.java)
        if (favoritePlayed != null && favoritePlayed.recentlyPlayed != null && favoritePlayed.recentlyPlayed.size > 0) run{
            mList!!.addAll(favoritePlayed.recentlyPlayed)
        }

    }

}