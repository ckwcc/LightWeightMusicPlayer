package com.ckw.lightweightmusicplayer.ui.localmusic;

import com.ckw.lightweightmusicplayer.di.FragmentScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ckw
 * on 2018/3/12.
 */
@Module
public abstract class LocalMusicModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract LocalMusicFragment localMusicFragment();
}
