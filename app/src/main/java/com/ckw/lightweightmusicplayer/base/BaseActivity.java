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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);

        if(needToolbar()){
            initToolbar();
        }

        //处理从其他界面传过来的数据
        handleIntent();

        initVariable();
        //view与数据绑定
        initView(savedInstanceState);

        initListener();

//        initAnimation();


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

    protected abstract void initVariable();

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

    /////////////////////////////////////////////////////////////////////////////////////////
    //ToolBar相关

    //返回false的时候，就不再需要重写setToolbar方法，当需要显示toolbar的时候，返回true
    protected abstract boolean needToolbar();
//    private boolean needToolbar(){
//        return true;
//    }

    private void initToolbar(){
        mToolbar =  findViewById(R.id.toolbar_id);
        if(mToolbar != null){
            setToolbar();
            setSupportActionBar(mToolbar);
            setNavigationIcon(R.drawable.ic_arrow_white_24dp);
        }

    }

    public abstract void setToolbar();

    public Toolbar getToolbar(){
        return mToolbar;
    }

    /**
     * 设置头部标题
     * @param title
     */
    public void setToolBarTitle(String title) {
        if(mToolbar != null){
            mToolbar.setTitle(title);
        }
    }

    public void setToolBarTitle(int resId) {
        if(mToolbar != null){
            mToolbar.setTitle(resId);
        }
    }

    /**
     * 自定义导航图标
     *
     * @param resId 图片的资源id
     */
    protected void setNavigationIcon(int resId) {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(resId);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNavigationIconClick();
                }
            });
        }
    }

    /**
     * 自定义导航栏图标
     *
     * @param drawable drawable对象
     */
    protected void setNavigationIcon(Drawable drawable) {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(drawable);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNavigationIconClick();
                }
            });
        }
    }



    /**
     * 设置toolbar的返回箭头是否显示
     *
     * @param enabled true:显示  false:不显示
     */
    protected void setDisplayHomeAsUpEnabled(boolean enabled) {
        if (mToolbar != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(enabled);
                if (enabled) {
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNavigationIconClick();
                        }
                    });
                }
            }
        }
    }

    /**
     * toolbar左侧返回键点击
     */
    protected void onNavigationIconClick() {
        onBackPressed();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
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
