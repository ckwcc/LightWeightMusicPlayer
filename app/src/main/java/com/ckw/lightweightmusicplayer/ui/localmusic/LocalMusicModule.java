package com.ckw.lightweightmusicplayer.ui.localmusic;

import com.ckw.lightweightmusicplayer.di.FragmentScoped;
import com.ckw.lightweightmusicplayer.ui.localmusic.fragments.LocalAlbumFragment;
import com.ckw.lightweightmusicplayer.ui.localmusic.fragments.LocalMusicListFragment;

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
    abstract LocalMusicListFragment localMusicFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract LocalAlbumFragment localAlbumFragment();

}
