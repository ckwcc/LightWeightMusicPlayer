package com.ckw.lightweightmusicplayer.ui.playmusic.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.ckw.lightweightmusicplayer.ui.playmusic.MutableMediaMetadata;
import com.ckw.lightweightmusicplayer.ui.playmusic.helper.MediaIdHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ckw
 * on 2018/3/19.
 * 提供本地数据
 */

public class MusicProvider {

    private SongSource mSongSource;

    private List<MediaMetadataCompat> mLocalMusicList;//本地所有的音乐

    private final ConcurrentMap<String, MutableMediaMetadata> mMusicListById;//key是musicId

    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByAlbum;//本地专辑集合

    private ConcurrentMap<String,List<MediaMetadataCompat>> mMusicListByArtist;//本地歌手集合

    public MusicProvider(Context context) {
        //提供本地数据
        mSongSource = new LocalSongSource(context);
        mLocalMusicList = new ArrayList<>();
        mMusicListById = new ConcurrentHashMap<>();
        mMusicListByAlbum = new ConcurrentHashMap<>();
        mMusicListByArtist = new ConcurrentHashMap<>();
    }

    public List<MediaMetadataCompat> getLocalMusic() {
        return mLocalMusicList;
    }

    /**
     * Return the MediaMetadataCompat for the given musicID.
     *
     * @param musicId The unique, non-hierarchical music ID.
     */
    public MediaMetadataCompat getMusic(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId).metadata : null;
    }

    /**
     * Get music tracks of the given albumTitle
     *
     */
    public List<MediaMetadataCompat> getMusicsByAlbum(String album) {
        return mMusicListByAlbum.get(album);
    }

    public List<MediaMetadataCompat> getMusicByArtist(String artist){
        return mMusicListByArtist.get(artist);
    }

    public synchronized void updateMusicArt(String musicId, Bitmap albumArt, Bitmap icon) {
        MediaMetadataCompat metadata = getMusic(musicId);
        metadata = new MediaMetadataCompat.Builder(metadata)

                // set high resolution bitmap in METADATA_KEY_ALBUM_ART. This is used, for
                // example, on the lockscreen background when the media session is active.
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)

                // set small version of the album art in the DISPLAY_ICON. This is used on
                // the MediaDescription and thus it should be small to be serialized if
                // necessary
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, icon)

                .build();

        MutableMediaMetadata mutableMetadata = mMusicListById.get(musicId);
        if (mutableMetadata == null) {
            throw new IllegalStateException("Unexpected error: Inconsistent data structures in " +
                    "MusicProvider");
        }

        mutableMetadata.metadata = metadata;
    }

    /*
    * 异步获取本地音乐
    * */
    @SuppressLint("StaticFieldLeak")
    public String retrieveMediaAsync(){
        final String[] retrieveState = new String[1];
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                retrieveLocalMedia();
                retrieveState[0] = "success";
                return retrieveState[0];
            }
        }.execute();

        return retrieveState[0];
    }

    /*
    * 获取本地音乐
    * */
    private synchronized void retrieveLocalMedia(){
        mLocalMusicList = mSongSource.getLocalList();

        Iterator<MediaMetadataCompat> tracks = mSongSource.iterator();
        while (tracks.hasNext()) {
            MediaMetadataCompat item = tracks.next();
            String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            mMusicListById.put(musicId, new MutableMediaMetadata(musicId, item));
        }

        buildListByAlbum();
        buildListByArtist();
    }

    /*
     * 将歌曲通过专辑类别 分类，返回一个（类别：list）的map
     * */
    private void buildListByAlbum() {
        ConcurrentMap<String, List<MediaMetadataCompat>> newMusicListByAlbum = new ConcurrentHashMap<>();
        for (MutableMediaMetadata m : mMusicListById.values()) {
            String albumTitle = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
            List<MediaMetadataCompat> list = newMusicListByAlbum.get(albumTitle);
            if (list == null) {
                list = new ArrayList<>();
                newMusicListByAlbum.put(albumTitle, list);
            }
            list.add(m.metadata);
        }
        mMusicListByAlbum = newMusicListByAlbum;
    }

    /*
    * 将歌曲通过歌手进行分类
    * */
    private void buildListByArtist(){
        ConcurrentMap<String,List<MediaMetadataCompat>> newMusicListByArtist = new ConcurrentHashMap<>();
        for (MutableMediaMetadata m :
                mMusicListById.values()) {
            String artist = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
            List<MediaMetadataCompat> list = newMusicListByArtist.get(artist);
            if(list == null){
                list = new ArrayList<>();
                newMusicListByArtist.put(artist,list);
            }
            list.add(m.metadata);
        }

        mMusicListByArtist = newMusicListByArtist;
    }



    /**
     * Get an iterator over the list of album+
     */
    public Iterable<String> getAlbumList() {
        return mMusicListByAlbum.keySet();
    }

    public Iterable<String> getArtistList(){
        return mMusicListByArtist.keySet();
    }


    /*
    * 返回本地音乐数据
    * */
    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaId){
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        //所有的音乐
        if(MediaIdHelper.MEDIA_ID_NORMAL.equals(mediaId)){
            for (MediaMetadataCompat mediaMetadataCompat: mLocalMusicList) {
                mediaItems.add(createMediaItem(mediaMetadataCompat));
            }
        }else if(MediaIdHelper.MEDIA_ID_ALBUM.equals(mediaId)){//专辑
            for (String albumTitle :
                    getAlbumList()) {
                mediaItems.add(createAlbumMediaItem(albumTitle));
            }
        }else if(mediaId.startsWith(MediaIdHelper.MEDIA_ID_ALBUM_DETAIL)){//专辑详情
            String[] split = mediaId.split("&");
            List<MediaMetadataCompat> musicsByAlbum = getMusicsByAlbum(split[1]);
            for (int i = 0; i < musicsByAlbum.size(); i++) {
                mediaItems.add(createMediaItem(musicsByAlbum.get(i)));
            }
        }else if(MediaIdHelper.MEDIA_ID_ARTIST.equals(mediaId)){//歌手列表
            for (String artistName :
                    getArtistList()) {
                mediaItems.add(createArtistMediaItem(artistName));
            }
        }else if(mediaId.startsWith(MediaIdHelper.MEDIA_ID_ARTIST_DETAIL)){//歌手列表详情
            String[] split = mediaId.split("&");
            List<MediaMetadataCompat> musicByArtist = getMusicByArtist(split[1]);
            for (int i = 0; i < musicByArtist.size(); i++) {
                mediaItems.add(createMediaItem(musicByArtist.get(i)));
            }
        }
        return mediaItems;
    }

    /*
    * 构建 歌手列表 数据
    * */
    private MediaBrowserCompat.MediaItem createArtistMediaItem(String artistName){
        List<MediaMetadataCompat> songList = mMusicListByArtist.get(artistName);//获得这个歌手的所有歌曲
        MediaMetadataCompat data = songList.get(0);

        MediaDescriptionCompat descriptionCompat = new MediaDescriptionCompat.Builder()
                .setMediaId(artistName)//歌手名称
                .setTitle(String.valueOf(songList.size()))
                .build();

        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }


    /*
    * 构建专辑数据
    * */
    private MediaBrowserCompat.MediaItem createAlbumMediaItem(String albumTitle){
        List<MediaMetadataCompat> albumList = mMusicListByAlbum.get(albumTitle);
        MediaMetadataCompat data = albumList.get(0);
        Uri imgUri = data.getDescription().getIconUri();//专辑的图片
        String artist = data.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(albumTitle)//专辑名称
                .setTitle(artist)//专辑作者
                .setSubtitle(String.valueOf(albumList.size()))//数量
                .setIconUri(imgUri)//专辑图片
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    /*
    * 构建单个数据
    * */
    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {

        String hierarchyAwareMediaID = metadata.getDescription().getMediaId();
        MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
                .build();
        return new MediaBrowserCompat.MediaItem(copy.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }



}
