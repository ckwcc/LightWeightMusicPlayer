package com.ckw.lightweightmusicplayer.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ckw.lightweightmusicplayer.ui.playmusic.MediaBrowserProvider;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

/**
 * Created by ckw
 * on 2017/12/7.
 */

public abstract class BaseFragment extends DaggerFragment implements BaseView{
    protected Context mContext;

    private Unbinder mUnbinder;

    public MediaBrowserProvider mediaBrowserProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        initVariables();
        handleArguments();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.mContext = context;
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("----", "onAttach: 调用了attach");
        mediaBrowserProvider = (MediaBrowserProvider) activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflater == null) {
            return null;
        }
        return inflater.inflate(getLayoutResID(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view == null) {
            return;
        }
        mUnbinder = ButterKnife.bind(this,view);

        operateViews(view);
        initListener();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    /**
     * 获取布局文件
     *
     * @return 布局文件的id
     */
    protected abstract int getLayoutResID();

    /**
     * 初始化变量---如list adapter FragmentManager
     */
    protected abstract void initVariables();

    /**
     * 处理Bundle
     */
    protected abstract void handleBundle(Bundle bundle);

    /**
     * 初始化控件
     *
     * @param view 布局文件的跟布局
     */
    protected abstract void operateViews(View view);

    /**
     * 初始化点击事件
     */
    protected abstract void initListener();


    //处理Activity界面传递过来的参数
    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            handleBundle(bundle);
        }
    }
}
