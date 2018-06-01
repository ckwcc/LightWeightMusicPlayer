package com.ckw.lightweightmusicplayer.di;

import com.ckw.lightweightmusicplayer.ui.about.AboutMeActivity;
import com.ckw.lightweightmusicplayer.ui.about.AboutMeModule;
import com.ckw.lightweightmusicplayer.ui.favorite.MyFavoriteActivity;
import com.ckw.lightweightmusicplayer.ui.favorite.MyFavoriteModule;
import com.ckw.lightweightmusicplayer.ui.localalbums.AlbumDetailActivity;
import com.ckw.lightweightmusicplayer.ui.localalbums.AlbumDetailModule;
import com.ckw.lightweightmusicplayer.ui.localalbums.LocalAlbumsActivity;
import com.ckw.lightweightmusicplayer.ui.localalbums.LocalAlbumsModule;
import com.ckw.lightweightmusicplayer.ui.localartists.LocalArtistsActivity;
import com.ckw.lightweightmusicplayer.ui.localartists.LocalArtistsModule;
import com.ckw.lightweightmusicplayer.ui.localsongs.LocalSongsActivity;
import com.ckw.lightweightmusicplayer.ui.localsongs.LocalSongsModule;
import com.ckw.lightweightmusicplayer.ui.login.LoginActivity;
import com.ckw.lightweightmusicplayer.ui.login.LoginModule;
import com.ckw.lightweightmusicplayer.ui.magic.MagicActivity;
import com.ckw.lightweightmusicplayer.ui.magic.MagicModule;
import com.ckw.lightweightmusicplayer.ui.main.MainActivity;
import com.ckw.lightweightmusicplayer.ui.main.MainActivityModule;
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

    @ActivityScoped
    @ContributesAndroidInjector(modules = AboutMeModule.class)
    abstract AboutMeActivity aboutMeActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = MyFavoriteModule.class)
    abstract MyFavoriteActivity MyFavoriteActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = LoginModule.class)
    abstract LoginActivity loginActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = MagicModule.class)
    abstract MagicActivity magicActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = LocalSongsModule.class)
    abstract LocalSongsActivity localSongsActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = LocalAlbumsModule.class)
    abstract LocalAlbumsActivity localAlbumsActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = AlbumDetailModule.class)
    abstract AlbumDetailActivity albumDetailActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = LocalArtistsModule.class)
    abstract LocalArtistsActivity localArtistsActivity();

}
