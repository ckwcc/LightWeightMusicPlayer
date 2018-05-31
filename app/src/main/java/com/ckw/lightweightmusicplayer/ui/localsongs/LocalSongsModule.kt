package com.ckw.lightweightmusicplayer.ui.localsongs

import com.ckw.lightweightmusicplayer.di.FragmentScoped

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by ckw
 * on 2018/5/31.
 */
@Module
abstract class LocalSongsModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun localSongsFragment(): LocalSongsFragment
}
