package com.ckw.lightweightmusicplayer.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseActivity;
import com.ckw.lightweightmusicplayer.repository.RecentBean;
import com.ckw.lightweightmusicplayer.repository.RecentlyPlayed;
import com.ckw.lightweightmusicplayer.ui.about.AboutMeActivity;
import com.ckw.lightweightmusicplayer.ui.favorite.MyFavoriteActivity;
import com.ckw.lightweightmusicplayer.ui.localalbums.LocalAlbumsActivity;
import com.ckw.lightweightmusicplayer.ui.localartists.LocalArtistsActivity;
import com.ckw.lightweightmusicplayer.ui.localmusic.LocalMusicActivity;
import com.ckw.lightweightmusicplayer.ui.localsongs.LocalSongsActivity;
import com.ckw.lightweightmusicplayer.ui.login.LoginActivity;
import com.ckw.lightweightmusicplayer.ui.magic.MagicActivity;
import com.ckw.lightweightmusicplayer.ui.playmusic.MusicPlayActivity;
import com.ckw.lightweightmusicplayer.utils.RecentUtils;
import com.ckw.lightweightmusicplayer.weight.CustomLinearGradient;
import com.ckw.lightweightmusicplayer.weight.EasyCountDownTextureView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.gson.Gson;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.drakeet.materialdialog.MaterialDialog;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper.MEDIA_ID_NORMAL;


public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener ,EasyPermissions.PermissionCallbacks, EasyCountDownTextureView.EasyCountDownListener {

    //在CustomLinearGradient中使用
    public static int themeColor = Color.parseColor("#B24242");
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    @BindView(R.id.toolbar_id)
    Toolbar mToolBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.fab_play)
    SpeedDialView mPlaySort;

    /*
    * 本地布局 点击
    * */
    @BindView(R.id.rl_local_song)
    RelativeLayout mRlLocalSong;
    @BindView(R.id.rl_local_album)
    RelativeLayout mRlLocalAlbum;
    @BindView(R.id.rl_local_artist)
    RelativeLayout mRlLocalArtist;
    //头部颜色渐变
    @BindView(R.id.custom_linear_gradient)
    CustomLinearGradient mCustomLinearGradient;
    //倒计时控件
    @BindView(R.id.easy_count_down_view)
    EasyCountDownTextureView mCountDownView;
    //最近播放
    @BindView(R.id.tv_recent_nothing)
    TextView mTvRecent;
    @BindView(R.id.rv_recent_list)
    RecyclerView mRvRecent;
    //我喜欢的
    @BindView(R.id.tv_favorite_list_nothing)
    TextView mTvFavoriteNothing;
    @BindView(R.id.tv_playlist_view_all)
    TextView mTvFavoriteAll;
    @BindView(R.id.rv_favorite_list)
    RecyclerView mRvFavorite;

    private String mMediaId;//分类id
    private RecentAdapter mRecentAdapter;
    private List<RecentBean> mRecentList;

    private RecentAdapter mFavoriteAdapter;
    private List<RecentBean> mFavoriteList;

    private MediaControllerCompat.TransportControls mController;
    private ImageView mIvLogin;
    private TextView mTvUserName;

    @Override
    protected void initView(Bundle savedInstanceState) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if(mDrawerLayout != null){
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
        mNavigationView.setNavigationItemSelectedListener(this);

        initNavigationHeaderView();
        requestPermission();

        initSpeedDialView();
        initRecentView();
        initFavoriteView();
    }


    @Override
    protected void handleBundle(@NonNull Bundle bundle) {

    }

    @Override
    protected void initVariable() {
        themeColor = SPUtils.getInstance().getInt("themeColor",Color.parseColor("#B24242"));
        mRecentList = new ArrayList<>();
        mFavoriteList = new ArrayList<>();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initListener() {
        mRlLocalSong.setOnClickListener(this);
        mRlLocalAlbum.setOnClickListener(this);
        mRlLocalArtist.setOnClickListener(this);
        mTvFavoriteAll.setOnClickListener(this);
//        mIvLogin.setOnClickListener(this);

        mRecentAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void setOnItemClick(int position, View view) {
                if(mRecentList.size() > position){
                    RecentBean recentBean = mRecentList.get(position);
                    String album = recentBean.getAlbum();
                    String mediaId = recentBean.getMediaId();

                    Bundle bundle = new Bundle();
                    bundle.putString("musicId",mediaId);
                    bundle.putString("iconUri",album);
                    bundle.putBoolean("play",true);
                    ActivityUtils.startActivity(bundle,MusicPlayActivity.class);
                }

            }
        });
        mFavoriteAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void setOnItemClick(int position, View view) {
                if(mFavoriteList.size() > position){
                    RecentBean recentBean = mFavoriteList.get(position);
                    String album = recentBean.getAlbum();
                    String mediaId = recentBean.getMediaId();

                    Bundle bundle = new Bundle();
                    bundle.putString("musicId",mediaId);
                    bundle.putString("iconUri",album);
                    bundle.putBoolean("play",true);
                    ActivityUtils.startActivity(bundle,MusicPlayActivity.class);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String login = SPUtils.getInstance().getString("login","");
        if(!login.equals("")){
            mTvUserName.setText(login);
        }

        showNavigationImageView();
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
    protected void onMediaControllerConnected(MediaSessionCompat.Token token) {
        super.onMediaControllerConnected(token);
        try {
            MediaControllerCompat mediaControllerCompat = new MediaControllerCompat(
                    MainActivity.this, token);
            mController = mediaControllerCompat.getTransportControls();
        } catch (RemoteException e) {
            e.printStackTrace();
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
                String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(this, perms)) {
                    ActivityUtils.startActivity(LocalMusicActivity.class);
                } else {
                    //继续申请，直到同意为止
                    EasyPermissions.requestPermissions(this,getResources().getString(R.string.need_permission_tip),REQUEST_READ_EXTERNAL_STORAGE,perms);
                }
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
            case R.id.nav_about_me:
                ActivityUtils.startActivity(AboutMeActivity.class);
                break;
            case R.id.nav_magic:
                ActivityUtils.startActivity(MagicActivity.class);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.rl_local_song://本地音乐(首页版本)
                String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(this, perms)) {
                    ActivityUtils.startActivity(LocalSongsActivity.class);
                } else {
                    //继续申请，直到同意为止
                    EasyPermissions.requestPermissions(this,getResources().getString(R.string.need_permission_tip),REQUEST_READ_EXTERNAL_STORAGE,perms);
                }
                break;
            case R.id.rl_local_album://本地专辑(首页版本)
                String[] permsAlbum = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(this, permsAlbum)) {
                    ActivityUtils.startActivity(LocalAlbumsActivity.class);
                } else {
                    //继续申请，直到同意为止
                    EasyPermissions.requestPermissions(this,getResources().getString(R.string.need_permission_tip),REQUEST_READ_EXTERNAL_STORAGE,permsAlbum);
                }
                break;
            case R.id.rl_local_artist:
                String[] permsArtist = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(this, permsArtist)) {
                    ActivityUtils.startActivity(LocalArtistsActivity.class);
                } else {
                    //继续申请，直到同意为止
                    EasyPermissions.requestPermissions(this,getResources().getString(R.string.need_permission_tip),REQUEST_READ_EXTERNAL_STORAGE,permsArtist);
                }
                break;
            case R.id.tv_playlist_view_all://查看全部
                if(mFavoriteList != null &&  mFavoriteList.size() > 0){
                    ActivityUtils.startActivity(MyFavoriteActivity.class);
                }
                break;
            case R.id.nav_image_view:
//                ActivityUtils.startActivity(LoginActivity.class);
                break;
        }
    }


    @Override
    public void onCountDownStart() {

    }

    @Override
    public void onCountDownTimeError() {

    }

    @Override
    public void onCountDownStop(long millisInFuture) {

    }

    /*
    * 倒计时完成
    * */
    @Override
    public void onCountDownCompleted() {
        mCountDownView.setVisibility(View.INVISIBLE);
        mController.stop();
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
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
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

    /*
    * 初始化NavigationView的头部
    * */
    private void initNavigationHeaderView(){
        View headerView = mNavigationView.getHeaderView(0);
        mIvLogin = headerView.findViewById(R.id.nav_image_view);
        mTvUserName = headerView.findViewById(R.id.tv_user_name);

        String login = SPUtils.getInstance().getString("login","");
        if(!login.equals("")){
            mTvUserName.setText(login);
        }
    }

    /*
     * 展示抽屉的头部
     * */
    private void showNavigationImageView(){
        String picture = SPUtils.getInstance().getString("picture", "");
        if(!picture.equals("")){
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.colorWhite)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this)
                    .load(picture)
                    .apply(options)
                    .into(mIvLogin);
        }else {
            mIvLogin.setImageResource(R.mipmap.bg_echelon);
        }

    }

    /*
    * 初始化播放按钮
    * */
    private void initSpeedDialView(){

        mPlaySort.addActionItem(
                new SpeedDialActionItem.Builder(R.id.action_use_case_two, getResources().getDrawable(android.R.drawable.ic_media_play))
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()))
                        .setLabel(getResources().getString(R.string.play_list))
                        .setLabelColor(getResources().getColor(R.color.colorAccent))
                        .create()
        );

        mPlaySort.addActionItem(
                new SpeedDialActionItem.Builder(R.id.action_use_case, getResources().getDrawable(android.R.drawable.ic_media_play))
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()))
                        .setLabel(getResources().getString(R.string.recent_play))
                        .setLabelColor(getResources().getColor(R.color.colorAccent))
                        .create()
        );


        mPlaySort.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()){
                    case R.id.action_use_case://最近播放
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
                            Snackbar.make(mPlaySort,R.string.recent_empty_tip,Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.action_use_case_two://我喜欢的
                        if(mFavoriteList != null && mFavoriteList.size() > 0){
                            String mediaId = mFavoriteList.get(0).getMediaId();
                            String album = mFavoriteList.get(0).getAlbum();

                            Bundle bundle = new Bundle();
                            bundle.putString("musicId",mediaId);
                            if (album != null) {
                                bundle.putString("iconUri",album);
                            }
                            bundle.putBoolean("play",true);
                            ActivityUtils.startActivity(bundle,MusicPlayActivity.class);
                        }else {
                            Snackbar.make(mPlaySort,R.string.favorite_empty_tip,Snackbar.LENGTH_SHORT).show();
                        }
                        break;

                }
                return false;
            }
        });

    }

    /*
    * 初始化最近播放列表
    * */
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

    /*
    * 初始化我喜欢的列表
    * */
    private void initFavoriteView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRvFavorite.setLayoutManager(linearLayoutManager);
        mFavoriteAdapter = new RecentAdapter(mFavoriteList,this);
        mRvFavorite.setAdapter(mFavoriteAdapter);

        String recent = SPUtils.getInstance().getString("favorite");
        Gson gson = new Gson();

        RecentlyPlayed recentlyPlayed = gson.fromJson(recent, RecentlyPlayed.class);
        if(recentlyPlayed != null && recentlyPlayed.getRecentlyPlayed() != null && recentlyPlayed.getRecentlyPlayed().size() > 0){
            mTvFavoriteNothing.setVisibility(View.GONE);
            mFavoriteList.addAll(recentlyPlayed.getRecentlyPlayed());
            mFavoriteAdapter.notifyDataSetChanged();
        }else {
            mTvFavoriteNothing.setVisibility(View.VISIBLE);
        }
    }

    /*
     * 定时关闭功能
     * */
    private void timerDialog() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle(getResources().getString(R.string.drawer_item_timer));
        View view = LayoutInflater.from(this).inflate(R.layout.view_select_time,null);
        mMaterialDialog.setContentView(view);
        TextView tvTenMin = view.findViewById(R.id.tv_ten_min);
        TextView tvTwentyMin = view.findViewById(R.id.tv_twenty_min);
        TextView tvThirtyMin = view.findViewById(R.id.tv_thirty_min);
        TextView tvFortyFiveMin = view.findViewById(R.id.forty_five_min);
        TextView tvOneHour = view.findViewById(R.id.tv_one_hour);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvTenMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownView.setTime(10*60*1000);
                mCountDownView.setVisibility(View.VISIBLE);
                mCountDownView.setEasyCountDownListener(MainActivity.this);
                mCountDownView.start();
                mMaterialDialog.dismiss();
            }
        });
        tvTwentyMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownView.setTime(20*60*1000);
                mCountDownView.setVisibility(View.VISIBLE);
                mCountDownView.start();
                mMaterialDialog.dismiss();
            }
        });
        tvThirtyMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownView.setTime(30*60*1000);
                mCountDownView.setVisibility(View.VISIBLE);
                mCountDownView.start();
                mMaterialDialog.dismiss();
            }
        });
        tvFortyFiveMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownView.setTime(45*60*1000);
                mCountDownView.setVisibility(View.VISIBLE);
                mCountDownView.start();
                mMaterialDialog.dismiss();
            }
        });
        tvOneHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownView.setTime(60*60*1000);
                mCountDownView.setVisibility(View.VISIBLE);
                mCountDownView.start();
                mMaterialDialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownView.stop();
                mCountDownView.setVisibility(View.INVISIBLE);
                mMaterialDialog.dismiss();
            }
        });

        mMaterialDialog.show();
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

    /*
    * 权限申请
    * */
    private void requestPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
                mRecentList.clear();
                mRecentList.addAll(recentlyPlayed.getRecentlyPlayed());
                mRecentAdapter.setData(mRecentList);
                mRecentAdapter.notifyDataSetChanged();
            }else {
                mRecentList.clear();
                mRecentAdapter.notifyDataSetChanged();
                mTvRecent.setVisibility(View.VISIBLE);
            }

            //这里做我的喜欢的刷新
            String favorite = SPUtils.getInstance().getString("favorite");
            Gson gsonFavorite = new Gson();

            RecentlyPlayed favoritePlayed = gsonFavorite.fromJson(favorite, RecentlyPlayed.class);
            if(favoritePlayed != null && favoritePlayed.getRecentlyPlayed() != null && favoritePlayed.getRecentlyPlayed().size() > 0){
                mTvFavoriteNothing.setVisibility(View.GONE);
                mFavoriteList.clear();
                mFavoriteList.addAll(favoritePlayed.getRecentlyPlayed());
                mFavoriteAdapter.setData(mFavoriteList);
                mFavoriteAdapter.notifyDataSetChanged();
            }else {
                mFavoriteList.clear();
                mFavoriteAdapter.notifyDataSetChanged();
                mTvFavoriteNothing.setVisibility(View.VISIBLE);
            }


        }
    };


    private void onConnected(){
        mMediaId = MEDIA_ID_NORMAL;
        getMediaBrowser().unsubscribe(mMediaId);
        getMediaBrowser().subscribe(mMediaId,mSubscriptionCallback);
    }


}
