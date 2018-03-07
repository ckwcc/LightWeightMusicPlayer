package com.ckw.lightweightmusicplayer.base;

/**
 * Created by ckw
 * on 2017/12/11.
 */

public interface BaseView {
    /**
     * 实现Baseview的View在这个方法里初始化presenter
     */
    void initPresenter();

    /**
     * 判断Fragment是否依附在Activity上
     *
     * @return true:依附在Activity上  false:没有依附在Activity上
     */
    boolean isActive();
}
