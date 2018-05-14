package com.ckw.lightweightmusicplayer.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;
import com.ckw.lightweightmusicplayer.repository.RecentBean;
import com.ckw.lightweightmusicplayer.repository.RecentlyPlayed;
import com.ckw.lightweightmusicplayer.ui.localmusic.LocalMusicActivity;
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity;
import com.ckw.lightweightmusicplayer.utils.RecentUtils;
import com.ckw.lightweightmusicplayer.weight.CustomLinearGradient;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.drakeet.materialdialog.MaterialDialog;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper.MEDIA_ID_NORMAL;


public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener ,EasyPermissions.PermissionCallbacks{

    //在CustomLinearGradient中使用
    public static int themeColor = Color.parseColor("#B24242");
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    @BindView(R.id.toolbar_id)
    Toolbar mToolBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.fab)
    FloatingActionButton mPlay;
    @BindView(R.id.rl_local_container)
    RelativeLayout mLocalMusicContainer;
    //头部颜色渐变
    @BindView(R.id.custom_linear_gradient)
    CustomLinearGradient mCustomLinearGradient;
    //最近播放
    @BindView(R.id.tv_recent_nothing)
    TextView mTvRecent;
    @BindView(R.id.rv_recent_list)
    RecyclerView mRvRecent;

    private String mMediaId;//分类id
    private RecentAdapter mRecentAdapter;
    private List<RecentBean> mRecentList;

    @Override
    protected void initView(Bundle savedInstanceState) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        requestPermission();

        initRecentView();
    }

    @Override
    protected void handleBundle(@NonNull Bundle bundle) {

    }

    @Override
    protected void initVariable() {
        themeColor = SPUtils.getInstance().getInt("themeColor",Color.parseColor("#B24242"));
        mRecentList = new ArrayList<>();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initListener() {
        mLocalMusicContainer.setOnClickListener(this);
        mPlay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.rl_local_container://本地音乐
                ActivityUtils.startActivity(LocalMusicActivity.class);
                break;
            case R.id.fab:
                if(mRecentList != null && mRecentList.size() > 0){
                    String mediaId = mRecentList.get(0).getMediaId();
                    String album = mRecentList.get(0).getAlbum();

                    Bundle bundle = new Bundle();
                    bundle.putString("musicId",mediaId);
                    if (album != null) {
                        bundle.putString("iconUri",album);
                    }
                    bundle.putBoolean("play",true);
                    ActivityUtils.startActivity(bundle,MusicPlayActivity.class);
                }else {
                    Snackbar.make(mPlay,R.string.recent_empty_tip,Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }


    @Override
    public void setToolbar() {
    }


    @Override
    protected void onMediaBrowserConnected() {
        super.onMediaBrowserConnected();
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            onConnected();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //暂时不想要了
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_home://回到主页

                break;
            case R.id.nav_local://本地音乐
                ActivityUtils.startActivity(LocalMusicActivity.class);
                break;
            case R.id.nav_time_close://定时关闭
                timerDialog();
                break;
            case R.id.nav_change_skin://个性换肤
                showChangeSkin();
                break;
            case R.id.nav_clear_recent:
                RecentUtils.clearRecent();
                mRecentList.clear();
                mRecentAdapter.notifyDataSetChanged();
                mTvRecent.setVisibility(View.VISIBLE);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void timerDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle(R.string.drawer_item_timer)
//                .setItems(getResources().getStringArray(R.array.timer_text), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                int[] times = getResources().getIntArray(R.array.timer_int);
//                                startTimer(times[which]);
//                            }
//                        }
//                )
//                .show();

        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle(getResources().getString(R.string.drawer_item_timer));
        mMaterialDialog.setMessage("Hello world!");
//        mMaterialDialog.setContentView()
        mMaterialDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });

        mMaterialDialog.show();
    }


    private void startTimer(int minute) {
//        QuitTimer.get().start(minute * 60 * 1000);
//        if (minute > 0) {
//            ToastUtils.show(activity.getString(R.string.timer_set, String.valueOf(minute)));
//        } else {
//            ToastUtils.show(R.string.timer_cancel);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(R.string.need_permission)
                    .setRationale(R.string.permission_tip)
                    .build()
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(this, perms)) {
                onConnected();
            } else {
                //继续申请，直到同意为止
                EasyPermissions.requestPermissions(this,getResources().getString(R.string.need_permission_tip),REQUEST_READ_EXTERNAL_STORAGE,perms);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initRecentView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRvRecent.setLayoutManager(linearLayoutManager);
        mRecentAdapter = new RecentAdapter(mRecentList,this);
        mRvRecent.setAdapter(mRecentAdapter);

        String recent = SPUtils.getInstance().getString("recent");
        Gson gson = new Gson();

        RecentlyPlayed recentlyPlayed = gson.fromJson(recent, RecentlyPlayed.class);
        if(recentlyPlayed != null && recentlyPlayed.getRecentlyPlayed() != null && recentlyPlayed.getRecentlyPlayed().size() > 0){
            mTvRecent.setVisibility(View.GONE);
            mRecentList.addAll(recentlyPlayed.getRecentlyPlayed());
            mRecentAdapter.notifyDataSetChanged();
        }else {
            mTvRecent.setVisibility(View.VISIBLE);
        }
    }



    /**
     * 展示显示主题换肤的dialog
     */
    private void showChangeSkin(){
        final AlertDialog dialog = ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.choose_theme_color)
                .initialColor(this.themeColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(9)
                .showColorPreview(true)
                .lightnessSliderOnly()
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                    }
                })
                .setPositiveButton(R.string.confirm, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int color, Integer[] allColors) {
                        setHomeActivityColor(color);
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .build();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(MainActivity.themeColor);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(MainActivity.themeColor);
            }
        });

        dialog.show();
    }


    /**
     * 设置相应的控件改变颜色
     * @param color 被选中的颜色
     */
    private void setHomeActivityColor(int color) {
        this.themeColor = color;
        SPUtils.getInstance().put("themeColor",themeColor);
        mCustomLinearGradient.setStartColor(themeColor);
        mCustomLinearGradient.invalidate();
    }

    private void requestPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            onConnected();
        } else {
            EasyPermissions.requestPermissions(this,getResources().getString(R.string.need_permission_tip),REQUEST_READ_EXTERNAL_STORAGE,perms);
        }

    }



    /*
     * 浏览器订阅的接口，数据的回调
     * */
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            //这里做最近列表的刷新
            String recent = SPUtils.getInstance().getString("recent");
            Gson gson = new Gson();

            RecentlyPlayed recentlyPlayed = gson.fromJson(recent, RecentlyPlayed.class);
            if(recentlyPlayed != null && recentlyPlayed.getRecentlyPlayed() != null && recentlyPlayed.getRecentlyPlayed().size() > 0){
                mTvRecent.setVisibility(View.GONE);
                mRecentList = recentlyPlayed.getRecentlyPlayed();
                mRecentAdapter.setData(mRecentList);
                mRecentAdapter.notifyDataSetChanged();
            }else {
                mTvRecent.setVisibility(View.VISIBLE);
            }

        }
    };

    private void onConnected(){
        mMediaId = MEDIA_ID_NORMAL;
        getMediaBrowser().unsubscribe(mMediaId);
        getMediaBrowser().subscribe(mMediaId,mSubscriptionCallback);
    }
}
