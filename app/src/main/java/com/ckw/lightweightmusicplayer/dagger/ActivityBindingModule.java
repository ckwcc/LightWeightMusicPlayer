package com.ckw.lightweightmusicplayer.dagger;

import com.ckw.lightweightmusicplayer.MainActivity;
import com.ckw.lightweightmusicplayer.MainActivityModule;

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
}
