package com.ckw.lightweightmusicplayer.ui.about

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import butterknife.BindView
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity

/**
 * Created by ckw
 * on 2018/5/17.
 */
class AboutMeActivity : BaseActivity() {

    private var mTvBlogAddress: TextView? = null
    private var mTvGithubAddress: TextView? = null
    private var mTvAppVersion: TextView? = null

    override fun handleBundle(bundle: Bundle) {
    }

    override fun initVariable() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_about_me
    }

    override fun initListener() {
        mTvBlogAddress!!.setOnClickListener {
            val uri = Uri.parse(getMyBlogAddress())
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        mTvGithubAddress!!.setOnClickListener {
            val uri = Uri.parse(getMyGithubAddress())
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }
    }

    override fun needToolbar(): Boolean {
        return true
    }

    override fun setToolbar() {
        setToolBarTitle(R.string.drawer_item_about)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvBlogAddress = findViewById(R.id.tv_blog_address)
        mTvBlogAddress!!.text = getMyBlogAddress()

        //两种非空 方式
        mTvGithubAddress = findViewById(R.id.tv_github_address)
        mTvGithubAddress!!.text = getMyGithubAddress()

        mTvAppVersion = findViewById(R.id.tv_app_version)
        mTvAppVersion?.text = getVersion()

    }

    /*
    * 获取博客地址
    * */
    private fun getMyBlogAddress(): String {
//        return String.format(resources.getString(R.string.my_blog_address),"https://blog.csdn.net/ckwccc")
        return "https://blog.csdn.net/ckwccc"
    }

    private fun getMyGithubAddress(): String {
//        return String.format(resources.getString(R.string.my_github_address),"https://github.com/ckwcc/LightWeightMusicPlayer")
        return "https://github.com/ckwcc/LightWeightMusicPlayer"
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    private fun getVersion(): String {
        return try {
            val packageManager = this.packageManager
            val info = packageManager.getPackageInfo(this.packageName,0)
            val versionName = info.versionName
            val version = String.format(resources.getString(R.string.current_version),versionName)
            version
        } catch (e: Exception) {
            "0";
        }

    }

}