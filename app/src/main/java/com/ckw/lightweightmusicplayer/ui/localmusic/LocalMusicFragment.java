package com.ckw.lightweightmusicplayer.ui.localmusic;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.ckw.lightweightmusicplayer.R;
import com.ckw.lightweightmusicplayer.base.BaseFragment;
import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.repository.Artist;
import com.ckw.lightweightmusicplayer.repository.LocalSong;
import com.ckw.lightweightmusicplayer.ui.localmusic.comparators.AlbumComparator;
import com.ckw.lightweightmusicplayer.ui.localmusic.comparators.ArtistComparator;
import com.ckw.lightweightmusicplayer.ui.localmusic.comparators.LocalMusicComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ckw
 * on 2018/3/12.
 */

public class LocalMusicFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks{

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    private List<LocalSong> mLocalSongs;//歌曲
    private List<Album> mAlbums;//专辑
    private List<Artist> mArtists;//歌手

    public static LocalMusicFragment newInstance() {
        Bundle args = new Bundle();
        LocalMusicFragment fragment = new LocalMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_local_music;
    }

    @Override
    protected void initVariables() {
        mLocalSongs = new ArrayList<>();
        mAlbums = new ArrayList<>();
        mArtists = new ArrayList<>();

        requestPermission();

    }

    @Override
    protected void handleBundle(Bundle bundle) {

    }

    @Override
    protected void operateViews(View view) {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode){
            case REQUEST_READ_EXTERNAL_STORAGE:
                getLocalSongs();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requestPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            getLocalSongs();
        } else {
            EasyPermissions.requestPermissions(this,"本地音乐需要读取内存权限",REQUEST_READ_EXTERNAL_STORAGE,perms);
        }

    }

    private void getLocalSongs(){
        mLocalSongs.clear();
        mAlbums.clear();
        mArtists.clear();

        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, MediaStore.MediaColumns.DATE_ADDED + " DESC");

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int pathColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DATA);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String path = musicCursor.getString(pathColumn);
                long duration = musicCursor.getLong(durationColumn);
                if (duration > 10000) {
                    LocalSong localSong = new LocalSong(thisId, thisTitle, thisArtist, thisAlbum, path, duration);
                    mLocalSongs.add(localSong);

                    int pos;
                    if (thisAlbum != null) {
                        pos = checkAlbum(thisAlbum);
                        if (pos != -1) {
                            mAlbums.get(pos).getmAlbumsongs().add(localSong);
                        } else {
                            List<LocalSong> llt = new ArrayList<>();
                            llt.add(localSong);
                            Album ab = new Album(thisAlbum, llt);
                            mAlbums.add(ab);
                        }
                    }

                    if (thisArtist != null) {
                        pos = checkArtist(thisArtist);
                        if (pos != -1) {
                            mArtists.get(pos).getmArtistsongs().add(localSong);
                        } else {
                            List<LocalSong> llt = new ArrayList<>();
                            llt.add(localSong);
                            Artist ab = new Artist(thisArtist, llt);
                            mArtists.add(ab);
                        }

                    }

                }

            }
            while (musicCursor.moveToNext());
        }

        if (musicCursor != null)
            musicCursor.close();

        try {
            Log.d("----", "operateViews: 本地音乐的数量："+mLocalSongs.size());
            Log.d("----", "operateViews: 本地专辑的数量："+mAlbums.size());
            Log.d("----", "operateViews: 本地歌手的数量："+mArtists.size());
            if (mLocalSongs.size() > 0) {
                Collections.sort(mLocalSongs, new LocalMusicComparator());
            }
            if (mAlbums.size() > 0) {
                Collections.sort(mAlbums, new AlbumComparator());
            }
            if (mArtists.size() > 0) {
                Collections.sort(mArtists, new ArtistComparator());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    /*
    * 检查专辑
    * */
    private int checkAlbum(String album) {
        for (int i = 0; i < mAlbums.size(); i++) {
            Album ab = mAlbums.get(i);
            if (ab.getName().equals(album)) {
                return i;
            }
        }
        return -1;
    }

    /*
    * 检查歌手
    * */
    private int checkArtist(String artist) {
        for (int i = 0; i < mArtists.size(); i++) {
            Artist at = mArtists.get(i);
            if (at.getName().equals(artist)) {
                return i;
            }
        }
        return -1;
    }


}
