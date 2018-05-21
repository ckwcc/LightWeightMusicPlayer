package com.ckw.lightweightmusicplayer.ui.favorite

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity
import com.ckw.lightweightmusicplayer.repository.RecentBean
import com.ckw.lightweightmusicplayer.repository.RecentlyPlayed
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity
import com.ckw.lightweightmusicplayer.utils.RecentUtils
import com.google.gson.Gson

class MyFavoriteActivity : BaseActivity(){

    private var mViewPager: ViewPager? = null
    private var mAdapter: PagesAdapter? = null
    private var mList: ArrayList<RecentBean>? = null
    private var mFragments: ArrayList<Fragment>? = null
    private var mFabPlay: FloatingActionButton? = null

    override fun initView(savedInstanceState: Bundle?) {
        findViews()
        getMyFavoriteData()
        initData()
        initViewPager()
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
        mFabPlay!!.setOnClickListener {
            val currentItem = mViewPager!!.currentItem
            val recentBean = mList!![currentItem]
            RecentUtils.addToRecent(recentBean)

            val bundle = Bundle()
            bundle.putString("musicId",recentBean.mediaId)
            if(recentBean.album != ""){
                bundle.putString("iconUri",recentBean.album)
            }
            bundle.putBoolean("play",true)
            ActivityUtils.startActivity(bundle,MusicPlayActivity::class.java)
        }
    }

    override fun needToolbar(): Boolean {
        return true
    }

    override fun setToolbar() {
        setToolBarTitle(R.string.play_list)
    }

    private fun findViews(): Unit {
        mFabPlay = findViewById(R.id.fab_favorite)
        mViewPager = findViewById(R.id.viewpager)
    }

    private fun initViewPager() {
        val selectedPage = 0
        mAdapter = PagesAdapter(supportFragmentManager,mFragments!!)
        mViewPager!!.adapter = mAdapter
        mViewPager!!.pageMargin = 40
        mViewPager!!.offscreenPageLimit = 2
        mViewPager!!.currentItem = selectedPage
        mViewPager!!.setPageTransformer(true, FilmPagerTransformer())
    }

    /*
    * 将数据注入fragment
    * */
    private fun initData(): Unit {
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

    /*
    * 从sp中获取数据
    * */
    private fun getMyFavoriteData(): Unit {
        val favorite = SPUtils.getInstance().getString("favorite")
        val gsonFavorite = Gson()

        var favoritePlayed = gsonFavorite.fromJson(favorite,RecentlyPlayed::class.java)
        if (favoritePlayed != null && favoritePlayed.recentlyPlayed != null && favoritePlayed.recentlyPlayed.size > 0) run{
            mList!!.addAll(favoritePlayed.recentlyPlayed)
        }
    }

}