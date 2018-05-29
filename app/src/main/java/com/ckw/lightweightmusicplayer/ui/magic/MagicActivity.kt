package com.ckw.lightweightmusicplayer.ui.magic

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ckw.lightweightmusicplayer.R
import com.ckw.lightweightmusicplayer.base.BaseActivity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import kotlinx.android.synthetic.main.activity_magic.*
import android.app.Activity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.entity.LocalMedia
import java.util.ArrayList


/**
 * Created by ckw
 * on 2018/5/28.
 */
class MagicActivity : BaseActivity(), View.OnClickListener {

    @BindView(R.id.rv_magic)
    lateinit var mRvMagic: RecyclerView

    @BindView(R.id.btn_apply_magic)
    lateinit var mBtnApply: Button

    @BindView(R.id.tv_magic_tip)
    lateinit var mTvTip: TextView

    private var selectList: ArrayList<LocalMedia>? = null
    private var mAdapter: GridImageAdapter? = null

    override fun initView(savedInstanceState: Bundle?) {
        val manager = FullyGridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        mRvMagic.setLayoutManager(manager)
        mAdapter = GridImageAdapter(this, onAddPicClickListener)
        mAdapter!!.setList(selectList!!)
        mAdapter!!.setSelectMax(1)
        mRvMagic.setAdapter(mAdapter)
    }

    override fun handleBundle(bundle: Bundle) {
    }

    override fun initVariable() {
        selectList = ArrayList()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_magic
    }

    override fun initListener() {
        mBtnApply.setOnClickListener(this)
    }

    override fun needToolbar(): Boolean {
        return true
    }

    override fun setToolbar() {
    }

    override fun onClick(v: View?) {
        val id = v!!.id
        when(id){
            R.id.btn_apply_magic ->{
                var path: String
                val media = selectList!![0]
                if(media.isCut && !media.isCompressed){
                    path = media.cutPath
                }else if(media.isCompressed || (media.isCut && media.isCompressed)){
                    path = media.compressPath
                }else{
                    path = media.path
                }

                SPUtils.getInstance().put("picture",path)

                ToastUtils.showShort("设置成功")

                finish()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>?

                    mAdapter!!.setList(selectList!!)
                    mAdapter!!.notifyDataSetChanged()

                    if(selectList!!.size != 0){
                        mBtnApply.visibility = View.VISIBLE
                        mTvTip.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private val onAddPicClickListener = object : GridImageAdapter.onAddPicClickListener {
        override fun onAddPicClick() {

            // 单独拍照
            PictureSelector.create(this@MagicActivity)
                    .openCamera(PictureConfig.TYPE_IMAGE)// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                    .maxSelectNum(9)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .selectionMode(PictureConfig.MULTIPLE)
                    .previewImage(true)// 是否可预览图片
                    .enablePreviewAudio(false) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .enableCrop(true)// 是否裁剪
                    .compress(true)// 是否压缩
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                    .isGif(false)// 是否显示gif图片
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .circleDimmedLayer(true)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(true)// 是否开启点击声音
                    .selectionMedia(selectList)// 是否传入已选图片
                    .previewEggs(false)//预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 裁剪压缩质量 默认为100
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled() // 裁剪是否可旋转图片
                    //.scaleEnabled()// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()////显示多少秒以内的视频or音频也可适用
                    .forResult(PictureConfig.CHOOSE_REQUEST)//结果回调onActivityResult code
        }

    }
}