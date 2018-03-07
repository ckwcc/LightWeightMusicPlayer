package com.ckw.lightweightmusicplayer.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.ckw.lightweightmusicplayer.R;

import javax.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by ckw
 * on 2017/12/7.
 */

public abstract class BaseActivity extends DaggerAppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private Toolbar mToolbar;

    private InputMethodManager imm;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);

        //处理从其他界面传过来的数据
        handleIntent();
        //view与数据绑定
        initView(savedInstanceState);

        initListener();

        initAnimation();


    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyBoard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.imm = null;

        if(mUnbinder != null){
            mUnbinder.unbind();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //所有的抽象方法

    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 处理上个界面传过来的数据---所有的Intent跳转的数据都需要包装在Bundle中
     *
     * @param bundle 界面跳转时传递的数据
     */
    protected abstract void handleBundle(@NonNull Bundle bundle);

    /**
     * this activity layout res
     * 设置layout布局,在子类重写该方法.
     * @return res layout xml id
     */
    protected abstract int getLayoutId();

    protected abstract void initListener();


    //检查是否可以有动画
    private boolean checkAnimationEnabled() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && animationEnabled();
    }

    //初始化动画
    private void initAnimation() {
        if (checkAnimationEnabled()) {
            setUpAnimation();
        }
    }

    /**
     * 设置需要转场动画---默认是true
     *
     * @return true: 需要 false: 不需要
     */
    protected boolean animationEnabled() {
        return true;
    }

    /**
     * 设置android 5.0的转场动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setUpAnimation() {

        Transition transition_enter = new Slide(Gravity.RIGHT);
        transition_enter.setDuration(600);
        getWindow().setEnterTransition(transition_enter);


        Transition transition_return = new Fade();
        transition_return.setDuration(400);
        getWindow().setReturnTransition(transition_return);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //其他

    //跳转界面时判读Intent是否携带数据
    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                handleBundle(bundle);
            }
        }
    }


    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    /**
     * 隐藏键盘
     */
    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.imm == null) {
            this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }
}
