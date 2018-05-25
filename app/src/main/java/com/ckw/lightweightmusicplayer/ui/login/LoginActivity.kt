package com.ckw.lightweightmusicplayer.ui.login

import android.os.Bundle
import android.widget.FrameLayout
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity

/**
 * Created by ckw
 * on 2018/5/25.
 */
class LoginActivity: BaseActivity() , LoginFragment.OnForgetListener, ForgetPasswordFragment.OnBackListener {

    override fun initView(savedInstanceState: Bundle?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fl_content,LoginFragment()).commit()
    }

    override fun handleBundle(bundle: Bundle) {
    }

    override fun initVariable() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initListener() {
    }

    override fun needToolbar(): Boolean {
        return true
    }

    override fun setToolbar() {
        setToolBarTitle(R.string.login)
    }

    override fun forgetPassword() {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.card_flip_left_in, R.animator.card_flip_left_out,
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out)
                .replace(R.id.fl_content,ForgetPasswordFragment())
                .addToBackStack(null).commit()
    }

    override fun goBack() {
        supportFragmentManager.popBackStack()
    }
}