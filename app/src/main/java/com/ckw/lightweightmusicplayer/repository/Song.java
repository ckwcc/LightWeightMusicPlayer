package com.ckw.lightweightmusicplayer.repository;

import android.os.Bundle;
import android.provider.MediaStore;

/**
 * Created by ckw
 * on 2018/3/12.
 */

public class Song {
    private String title, titleKey, artist, artistKey,
            album, albumKey, displayName, mimeType, path;
    private int id, albumId, artistId, duration, size, year, track;
    private boolean isRingtone, isPodcast, isAlarm, isMusic, isNotification;

    //private File mCoverFile;

    private Album albumObj;

    public Song(int id,String title, String artist, String album, String path, int duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.path = path;
        this.duration = duration;
    }


    public Song (Bundle bundle) {
        id = bundle.getInt(MediaStore.Audio.Media._ID);
        title = bundle.getString(MediaStore.Audio.Media.TITLE);
        titleKey = bundle.getString(MediaStore.Audio.Media.TITLE_KEY);
        artist = bundle.getString(MediaStore.Audio.Media.ARTIST);
        artistKey = bundle.getString(MediaStore.Audio.Media.ARTIST_KEY);
        album = bundle.getString(MediaStore.Audio.Media.ALBUM);
        albumKey = bundle.getString(MediaStore.Audio.Media.ALBUM_KEY);
        displayName = bundle.getString(MediaStore.Audio.Media.DISPLAY_NAME);
        year = bundle.getInt(MediaStore.Audio.Media.YEAR);
        mimeType = bundle.getString(MediaStore.Audio.Media.MIME_TYPE);
        path = bundle.getString(MediaStore.Audio.Media.DATA);

        artistId = bundle.getInt(MediaStore.Audio.Media.ARTIST_ID);
        albumId = bundle.getInt(MediaStore.Audio.Media.ALBUM_ID);
        track = bundle.getInt(MediaStore.Audio.Media.TRACK);
        duration = bundle.getInt(MediaStore.Audio.Media.DURATION);
        size = bundle.getInt(MediaStore.Audio.Media.SIZE);
        isRingtone = bundle.getInt(MediaStore.Audio.Media.IS_RINGTONE) == 1;
        isPodcast = bundle.getInt(MediaStore.Audio.Media.IS_PODCAST) == 1;
        isAlarm = bundle.getInt(MediaStore.Audio.Media.IS_ALARM) == 1;
        isMusic = bundle.getInt(MediaStore.Audio.Media.IS_MUSIC) == 1;
        isNotification = bundle.getInt(MediaStore.Audio.Media.IS_NOTIFICATION) == 1;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getPath() {
        return path;
    }

    public int getDuration() {
        return duration;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public String getArtistKey() {
        return artistKey;
    }

    public String getAlbumKey() {
        return albumKey;
    }

    public int getAlbumId() {
        return albumId;
    }

    public int getArtistId() {
        return artistId;
    }

    public int getYear() {
        return year;
    }

    public int getTrack() {
        return track;
    }

    public int getSize() {
        return size;
    }

    public boolean isRingtone() {
        return isRingtone;
    }

    public boolean isPodcast() {
        return isPodcast;
    }

    public boolean isAlarm() {
        return isAlarm;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public boolean isNotification() {
        return isNotification;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Song) {
            return ((Song) obj).id == id;
        }
        return false;
    }

    /*public File getCoverFile (Context context) {
        if (mCoverFile == null) {
            mCoverFile = new File(context.getExternalCacheDir(), "covers" + File.separator + getTitle() + "_" + getArtist() + "_" + getAlbum() + ".jpg");
        }
        return mCoverFile;
    }*/

    public String getArtistAlbum () {
        return getArtist() + " - " + getAlbum();
    }

    public Album getAlbumObj() {
        return albumObj;
    }

    public void setAlbumObj(Album albumObj) {
        this.albumObj = albumObj;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", titleKey='" + titleKey + '\'' +
                ", artist='" + artist + '\'' +
                ", artistKey='" + artistKey + '\'' +
                ", album='" + album + '\'' +
                ", albumKey='" + albumKey + '\'' +
                ", displayName='" + displayName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", path='" + path + '\'' +
                ", id=" + id +
                ", albumId=" + albumId +
                ", artistId=" + artistId +
                ", duration=" + duration +
                ", size=" + size +
                ", year=" + year +
                ", track=" + track +
                ", isRingtone=" + isRingtone +
                ", isPodcast=" + isPodcast +
                ", isAlarm=" + isAlarm +
                ", isMusic=" + isMusic +
                ", isNotification=" + isNotification +
                ", albumObj=" + albumObj +
                '}';
    }
}
