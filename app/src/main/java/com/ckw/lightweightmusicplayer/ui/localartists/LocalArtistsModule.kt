package com.ckw.lightweightmusicplayer.ui.localartists

import com.ckw.lightweightmusicplayer.di.FragmentScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by ckw
 * on 2018/6/1.
 */
@Module
abstract class LocalArtistsModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun localArtistsFragment(): LocalArtistsFragment
}