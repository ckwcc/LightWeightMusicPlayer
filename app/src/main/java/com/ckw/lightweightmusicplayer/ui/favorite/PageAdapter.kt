package com.ckw.lightweightmusicplayer.ui.favorite

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class PageAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): android.support.v4.app.Fragment {
        val bundle = Bundle()
        bundle.putInt(PlaceholderFragment.EXTRA_POSITION, position + 1)

        val fragment = PlaceholderFragment()
        fragment.arguments = bundle

        return fragment
    }

    override fun getCount(): Int {
        return 5
    }

}

