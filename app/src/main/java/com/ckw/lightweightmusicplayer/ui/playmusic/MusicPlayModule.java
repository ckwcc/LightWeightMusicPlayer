package com.ckw.lightweightmusicplayer.ui.playmusic;

import com.ckw.lightweightmusicplayer.di.FragmentScoped;
import com.ckw.lightweightmusicplayer.ui.localmusic.fragments.LocalMusicListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ckw
 * on 2018/3/14.
 */
@Module
public abstract class MusicPlayModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract MusicPlayFragment musicPlayFragment();
}
