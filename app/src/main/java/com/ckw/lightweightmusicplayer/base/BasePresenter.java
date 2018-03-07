package com.ckw.lightweightmusicplayer.base;

/**
 * Created by ckw
 * on 2017/12/11.
 */

public interface BasePresenter<T> {
    /**
     * Binds presenter with a view when resumed. The Presenter will perform initialization here.
     *
     * @param view the view associated with this presenter
     */
    void takeView(T view);

    /**
     * Drops the reference to the view when destroyed
     */
    void dropView();
}
