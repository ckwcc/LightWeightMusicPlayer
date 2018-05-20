package com.ckw.lightweightmusicplayer.ui.favorite

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class PagesAdapter : FragmentPagerAdapter {

    private var mFragments: List<Fragment>? = null

    constructor(fm: FragmentManager) : super(fm)

    constructor(fm: FragmentManager, fragments: List<Fragment>) : super(fm) {
        mFragments = fragments
    }

    override fun getItem(position: Int): Fragment {
        return mFragments!![position]
    }

    override fun getCount(): Int {
        return mFragments!!.size
    }
}
