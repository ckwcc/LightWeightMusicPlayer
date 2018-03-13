package com.ckw.lightweightmusicplayer.ui.localmusic;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.blankj.utilcode.util.FragmentUtils;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * Created by ckw
 * on 2018/3/12.
 */

public class LocalMusicActivity extends BaseActivity {

    private LocalMusicFragment localMusicFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {

        LocalMusicFragment localMusicFragment = (LocalMusicFragment) getSupportFragmentManager().findFragmentById(R.id.rl_common_content);
        if (localMusicFragment == null) {
            localMusicFragment = LocalMusicFragment.newInstance();
            FragmentUtils.add(getSupportFragmentManager(),localMusicFragment,R.id.rl_common_content);

        }
    }


    @Override
    protected void handleBundle(@NonNull Bundle bundle) {

    }

    @Override
    protected void initVariable() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common;
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void setToolbar() {
        setToolBarTitle(R.string.drawer_item_local);
    }




}
