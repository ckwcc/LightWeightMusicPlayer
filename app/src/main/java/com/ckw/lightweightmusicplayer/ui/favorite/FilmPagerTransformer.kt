package com.ckw.lightweightmusicplayer.ui.favorite

import android.support.v4.view.ViewPager
import android.view.View

class FilmPagerTransformer : ViewPager.PageTransformer {

    private val mMinAlpha = 0.6f
    private val transitionY = 90.0f

    override fun transformPage(page: View, position: Float) {
        if (position <= -1 || position >= 1) {//中间两边的ViewPager，已经彻底移出
            page.alpha = mMinAlpha
            page.translationY = transitionY
        } else if (position < 1) {//范围是：[-1,1]
            //从中间向左或向右一点点移动
            if (position > -1 && position < 0) {
                val factor = mMinAlpha + (1 - mMinAlpha) * (1 + position)
                val realTransition = transitionY * (-1 * position)
                page.alpha = factor
                page.translationY = realTransition
            } else if (position < 1 && position > 0) {
                val factor = mMinAlpha + (1 - mMinAlpha) * (1 - position)
                val realTransition = transitionY * position
                page.alpha = factor
                page.translationY = realTransition
            }
        }
    }
}
