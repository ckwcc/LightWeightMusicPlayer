package com.ckw.lightweightmusicplayer.repository;

/**
 * Created by ckw
 * on 2018/5/10.
 */
public class RecentBean {
    private String mediaId;
    private String title;
    private String artist;
    private String album;

    public RecentBean() {
    }

    public RecentBean(String mediaId, String title, String artist, String album) {
        this.mediaId = mediaId;
        this.title = title;
        this.artist = artist;
        this.album = album;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
