package com.ckw.lightweightmusicplayer.ui.login

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import butterknife.BindView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseFragment

/**
 * Created by ckw
 * on 2018/5/25.
 */
class LoginFragment : BaseFragment(), View.OnClickListener {

    @BindView(R.id.tv_forget_pwd)
    lateinit var mTvForget: TextView

    @BindView(R.id.et_user_name)
    lateinit var mEtUserName: EditText

    @BindView(R.id.cb_auto_login)
    lateinit var mCbAutoLogin: CheckBox

    @BindView(R.id.btn_register)
    lateinit var mBtnRegister: Button

    @BindView(R.id.btn_login)
    lateinit var mBtnLogin: Button

    internal var mCallback: OnForgetListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mCallback = context as OnForgetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "must implement OnForgetListener")
        }

    }
    override fun getLayoutResID(): Int {
        return R.layout.fragment_login
    }

    override fun initVariables() {
    }

    override fun handleBundle(bundle: Bundle?) {
    }

    override fun operateViews(view: View?) {
        val login = SPUtils.getInstance().getString("login","")
        if(login != ""){
            mEtUserName.setText(login)
        }
    }

    override fun initListener() {
        mTvForget.setOnClickListener(this)
        mBtnLogin.setOnClickListener(this)
        mCbAutoLogin.setOnClickListener(this)
        mBtnRegister.setOnClickListener(this)
    }

    override fun initPresenter() {
    }

    override fun onClick(v: View?) {
        val id = v!!.id
        when(id){
             R.id.tv_forget_pwd -> { mCallback!!.forgetPassword()}
            R.id.btn_login -> {
                val userName = mEtUserName.text.toString().trim()
                if(userName != "" ){
                    SPUtils.getInstance().put("login",userName)
                    activity!!.finish()
                }else{
                    ToastUtils.showShort("用户名为空哟")
                }
            }
            R.id.cb_auto_login -> {
                ToastUtils.showShort("偷偷告诉你，这里点了也白点哟")
            }
            R.id.btn_register -> {
                ToastUtils.showShort("注册什么的，完全不需要！")
            }
        }
    }

    interface OnForgetListener {
        fun forgetPassword()
    }
}