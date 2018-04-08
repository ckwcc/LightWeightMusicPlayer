package com.ckw.lightweightmusicplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ckw.lightweightmusicplayer.base.BaseActivity;
import com.ckw.lightweightmusicplayer.ui.localmusic.LocalMusicActivity;
import com.ckw.lightweightmusicplayer.weight.RotaryTableInfo;
import com.ckw.lightweightmusicplayer.weight.RotaryTableView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.ckw.lightweightmusicplayer.common.Constant.LOCAL_SONG_PERMISSION;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener ,EasyPermissions.PermissionCallbacks{

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
    @BindView(R.id.rl_favourite_container)
    RelativeLayout mFavouriteContainer;



    @Override
    protected void initView(Bundle savedInstanceState) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        requestPermission();
    }

    @Override
    protected void handleBundle(@NonNull Bundle bundle) {

    }

    @Override
    protected void initVariable() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initListener() {
        mLocalMusicContainer.setOnClickListener(this);
        mFavouriteContainer.setOnClickListener(this);
        mPlay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.rl_local_container://本地音乐
                ActivityUtils.startActivity(LocalMusicActivity.class);
                break;
            case R.id.rl_favourite_container://我喜欢的
                showChangeSkin();
                break;
            case R.id.fab:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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

                break;
            case R.id.nav_change_skin://个性换肤
                showChangeSkin();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /**
     * 展示显示主题换肤的dialog
     */
    private void showChangeSkin(){
        final AlertDialog dialog = ColorPickerDialogBuilder
                .with(this)
                .setTitle("请选择主题颜色")
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
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int color, Integer[] allColors) {
                        setHomeActivityColor(color);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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
    }

    private void requestPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            SPUtils.getInstance().put(LOCAL_SONG_PERMISSION,true);
        } else {
            EasyPermissions.requestPermissions(this,"本地音乐需要读取内存权限",REQUEST_READ_EXTERNAL_STORAGE,perms);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        SPUtils.getInstance().put(LOCAL_SONG_PERMISSION,true);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限需要")
                    .setRationale("音乐季需要获得访问您设备上的音乐的权限，现在去设置界面打开它好吗？")
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
                SPUtils.getInstance().put(LOCAL_SONG_PERMISSION,true);
            } else {
                //继续申请，直到同意为止
                EasyPermissions.requestPermissions(this,"本地音乐需要读取内存权限",REQUEST_READ_EXTERNAL_STORAGE,perms);
            }
        }
    }
}
