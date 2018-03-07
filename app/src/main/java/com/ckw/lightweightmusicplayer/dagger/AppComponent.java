package com.ckw.lightweightmusicplayer.dagger;

import android.app.Application;
import android.content.Context;

import com.ckw.lightweightmusicplayer.CkwApplication;
import com.ckw.lightweightmusicplayer.NetLoader.ApiService;
import com.ckw.lightweightmusicplayer.NetLoader.HttpManager;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import retrofit2.Retrofit;

/**
 * Created by ckw
 * on 2017/12/7.
 */
@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
                      ActivityBindingModule.class,
                      AppModule.class,
                      NetModule.class
})
public interface AppComponent extends AndroidInjector<CkwApplication> {

    Context getContext();

    Gson getGson();

    ApiService getApiService();

    HttpManager getHttpManager();

    Retrofit getRetrofit();

    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
