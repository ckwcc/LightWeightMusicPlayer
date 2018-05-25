package com.ckw.lightweightmusicplayer.ui.login

import com.ckw.lightweightmusicplayer.di.FragmentScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by ckw
 * on 2018/5/25.
 */
@Module
abstract class LoginModule {
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun loginFragment(): LoginFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun forgetPasswordFragment(): ForgetPasswordFragment

}