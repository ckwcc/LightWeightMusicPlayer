package com.ckw.lightweightmusicplayer.di;

import com.ckw.lightweightmusicplayer.MainActivity;
import com.ckw.lightweightmusicplayer.MainActivityModule;
import com.ckw.lightweightmusicplayer.ui.localmusic.LocalMusicActivity;
import com.ckw.lightweightmusicplayer.ui.localmusic.LocalMusicModule;
import com.ckw.lightweightmusicplayer.ui.localmusic.activities.AlbumActivity;
import com.ckw.lightweightmusicplayer.ui.localmusic.activities.ArtistActivity;
import com.ckw.lightweightmusicplayer.ui.localmusic.activities.modules.AlbumModule;
import com.ckw.lightweightmusicplayer.ui.localmusic.activities.modules.ArtistModule;
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity;
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ckw
 * on 2018/3/7.
 * 所有的activity的绑定在这里
 */
@Module
public abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity mainActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = LocalMusicModule.class)
    abstract LocalMusicActivity localMusicActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = AlbumModule.class)
    abstract AlbumActivity albumActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ArtistModule.class)
    abstract ArtistActivity artistActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = MusicPlayModule.class)
    abstract MusicPlayActivity musicPlayActivity();


}
