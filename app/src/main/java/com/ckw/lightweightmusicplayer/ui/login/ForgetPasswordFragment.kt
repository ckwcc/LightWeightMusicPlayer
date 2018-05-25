package com.ckw.lightweightmusicplayer.ui.login

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import butterknife.BindView
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseFragment

/**
 * Created by ckw
 * on 2018/5/25.
 */
class ForgetPasswordFragment : BaseFragment(), View.OnClickListener {

    @BindView(R.id.btn_back)
    lateinit var btnBack: Button

    internal var mCallback: OnBackListener ?= null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mCallback = activity as OnBackListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement OnBackListener")
        }

    }
    override fun getLayoutResID(): Int {
        return R.layout.fragment_forget_password
    }

    override fun initVariables() {
    }

    override fun handleBundle(bundle: Bundle?) {
    }

    override fun operateViews(view: View?) {
    }

    override fun initListener() {
        btnBack.setOnClickListener(this)
    }

    override fun initPresenter() {
    }

    override fun onClick(v: View?) {
        val id = v!!.id
        when(id){
            R.id.btn_back ->{ mCallback!!.goBack()}
        }
    }

    interface OnBackListener {
        fun goBack()
    }
}