package com.ckw.lightweightmusicplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.ckw.lightweightmusicplayer.repository.Album;
import com.ckw.lightweightmusicplayer.repository.Artist;
import com.ckw.lightweightmusicplayer.repository.Song;
import com.ckw.lightweightmusicplayer.ui.localmusic.ArtistComparator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ckw
 * on 2018/3/13.
 */

public class MediaUtils {

    private static final String TAG = MediaUtils.class.getSimpleName();

    public static final DecimalFormat FORMAT = new DecimalFormat("00");

    public static final String[] AUDIO_KEYS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TITLE_KEY,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST_KEY,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM_KEY,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_PODCAST,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATA,
    };

    public static final String[] ALBUM_COLUMNS = new String[] {
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ALBUM_KEY,
            MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.FIRST_YEAR,
            MediaStore.Audio.Albums.LAST_YEAR,
    };

    public static final String[] ARTIST_COLUMNS = new String[] {
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.ARTIST_KEY,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
    };

    /*
    * 返回的song中只有歌手的名字，没什么用
    * */
//    public static List<Song> getArtistList (Context context) {
//        ContentResolver resolver = context.getContentResolver();
//        Cursor cursor = resolver.query(
//                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
//                ARTIST_COLUMNS,
//                null,
//                null,
//                null);
//        return getAudioList(cursor);
//    }

    public static List<Artist> getArtistList(Context context){
        List<Artist> artists = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
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
                int thisId = (int) musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String path = musicCursor.getString(pathColumn);
                int duration = (int) musicCursor.getLong(durationColumn);
                if (duration > 10000) {
                    Song lt = new Song(thisId, thisTitle, thisArtist, thisAlbum, path, duration);

                    int pos;

                    if (thisArtist != null) {
                        pos = checkArtist(thisArtist,artists);
                        if (pos != -1) {
                            artists.get(pos).getArtistsongs().add(lt);
                        } else {
                            List<Song> llt = new ArrayList<>();
                            llt.add(lt);
                            Artist ab = new Artist(thisArtist, llt);
                            artists.add(ab);
                        }
                    }

                }

            }
            while (musicCursor.moveToNext());
        }

        if (musicCursor != null)
            musicCursor.close();

        try {
            if (artists.size() > 0) {
                Collections.sort(artists, new ArtistComparator());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return artists;

    }

    private static int checkArtist(String artist,List<Artist> artists) {
        for (int i = 0; i < artists.size(); i++) {
            Artist at = artists.get(i);
            if (at.getName().equals(artist)) {
                return i;
            }
        }
        return -1;
    }

    public static List<Song> getAlbumSongList (Context context, int album_id) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_KEYS,
                MediaStore.Audio.Media.ALBUM_ID + " = " + album_id,
                null,
                null);
        return getAudioList(cursor);
    }

    public static List<Album> getAlbumList (Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                ALBUM_COLUMNS,
                null,
                null,
                null);
        int count = cursor.getCount();
        List<Album> albumList = null;
        if (count > 0) {
            albumList = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id, minYear, maxYear, numSongs;
                String album, albumKey, artist, albumArt;
                id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                minYear = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR));
                maxYear = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
                numSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
                album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_KEY));
                artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

                Album albumObj = new Album(id, minYear, maxYear, numSongs,
                        album, albumKey, artist, albumArt);
                albumList.add(albumObj);
            }

        }
        cursor.close();
        return albumList;
    }

    public static List<Song> getAudioList(Context context) {


        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_KEYS,
                MediaStore.Audio.Media.IS_MUSIC + "=" + 1,
                null,
                null);
        return getAudioList(cursor);
    }

    private static List<Song> getAudioList (Cursor cursor) {
        List<Song> audioList = null;
        if (cursor.getCount() > 0) {
            audioList = new ArrayList<Song>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Bundle bundle = new Bundle();
                for (int i = 0; i < AUDIO_KEYS.length; i++) {
                    final String key = AUDIO_KEYS[i];
                    final int columnIndex = cursor.getColumnIndex(key);
                    final int type = cursor.getType(columnIndex);
                    switch (type) {
                        case Cursor.FIELD_TYPE_BLOB:
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            float floatValue = cursor.getFloat(columnIndex);
                            bundle.putFloat(key, floatValue);
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            int intValue = cursor.getInt(columnIndex);
                            bundle.putInt(key, intValue);
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            String strValue = cursor.getString(columnIndex);
                            bundle.putString(key, strValue);
                            break;
                    }
                }
                Song audio = new Song(bundle);
                audioList.add(audio);
            }
        }

        cursor.close();
        return audioList;
    }



    public static String formatTime (int durationInMilliseconds) {
        int seconds = durationInMilliseconds /  1000;
        int minutes = seconds / 60;
        int secondsRemain = seconds % 60;
        return FORMAT.format(minutes) + ":" + FORMAT.format(secondsRemain);
    }
}
