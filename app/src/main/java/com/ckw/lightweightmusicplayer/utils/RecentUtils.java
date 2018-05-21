package com.ckw.lightweightmusicplayer.utils;

import android.support.v4.media.MediaBrowserCompat;

import com.blankj.utilcode.util.SPUtils;
import com.ckw.lightweightmusicplayer.repository.RecentBean;
import com.ckw.lightweightmusicplayer.repository.RecentlyPlayed;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckw
 * on 2018/5/14.
 * 将歌曲添加到最近听歌列表
 */
public class RecentUtils {

    /*
    * 检查是否是我的喜欢
    * */
    public static boolean isFavorite(String mediaId){
        String favorite = SPUtils.getInstance().getString("favorite");
        Gson gson = new Gson();
        RecentlyPlayed recentlyPlayed = gson.fromJson(favorite, RecentlyPlayed.class);

        if(recentlyPlayed != null && recentlyPlayed.getRecentlyPlayed() != null && recentlyPlayed.getRecentlyPlayed().size() > 0){
            boolean checkMediaId = checkMediaId(mediaId, recentlyPlayed.getRecentlyPlayed());
            if(checkMediaId){//如果有这首歌
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    /*
    * 删除我的喜欢
    * */
    public static void removeTheFavorite(String mediaId){
        String favorite = SPUtils.getInstance().getString("favorite");
        Gson gson = new Gson();
        RecentlyPlayed recentlyPlayed = gson.fromJson(favorite, RecentlyPlayed.class);

        if(recentlyPlayed != null && recentlyPlayed.getRecentlyPlayed() != null && recentlyPlayed.getRecentlyPlayed().size() > 0){
            boolean checkMediaId = checkMediaId(mediaId, recentlyPlayed.getRecentlyPlayed());
            if(checkMediaId){
                int mediaIdPosition = getMediaIdPosition(mediaId, recentlyPlayed.getRecentlyPlayed());
                if(mediaIdPosition != -1){
                    recentlyPlayed.getRecentlyPlayed().remove(mediaIdPosition);
                }
            }
            String toJson = gson.toJson(recentlyPlayed);
            SPUtils.getInstance().put("favorite",toJson);
        }
    }

    /*
    * 添加我的喜欢
    * */
    public static void addToFavorite(RecentBean recentBean){
        String favorite = SPUtils.getInstance().getString("favorite");
        Gson gson = new Gson();
        RecentlyPlayed recentlyPlayed = gson.fromJson(favorite, RecentlyPlayed.class);

        if(recentlyPlayed != null && recentlyPlayed.getRecentlyPlayed() != null && recentlyPlayed.getRecentlyPlayed().size() > 0){
            boolean checkMediaId = checkMediaId(recentBean.getMediaId(), recentlyPlayed.getRecentlyPlayed());
            if(!checkMediaId){
                recentlyPlayed.getRecentlyPlayed().add(0,recentBean);
            }
            String toJson = gson.toJson(recentlyPlayed);
            SPUtils.getInstance().put("favorite",toJson);
        }else {
            List<RecentBean> list = new ArrayList<>();
            RecentlyPlayed played = new RecentlyPlayed();
            played.setRecentlyPlayed(list);
            played.getRecentlyPlayed().add(0,recentBean);
            String toJson = gson.toJson(played);
            SPUtils.getInstance().put("favorite",toJson);
        }
    }

    public static void addToRecent(RecentBean addBean){
        String recent = SPUtils.getInstance().getString("recent");
        Gson gson = new Gson();
        RecentlyPlayed recentlyPlayed = gson.fromJson(recent, RecentlyPlayed.class);
        if(recentlyPlayed != null && recentlyPlayed.getRecentlyPlayed() != null && recentlyPlayed.getRecentlyPlayed().size() > 0){
            boolean checkMediaId = checkMediaId(addBean.getMediaId(), recentlyPlayed.getRecentlyPlayed());
            if(!checkMediaId){
                RecentBean recentBean = new RecentBean();
                recentBean.setMediaId(addBean.getMediaId());
                recentBean.setTitle(addBean.getTitle().toString());
                recentBean.setArtist(addBean.getTitle());
                if(addBean.getAlbum() != null){
                    recentBean.setAlbum(addBean.getAlbum());
                }
                recentlyPlayed.getRecentlyPlayed().add(0,recentBean);
            }
            String toJson = gson.toJson(recentlyPlayed);
            SPUtils.getInstance().put("recent",toJson);
        }else {
            List<RecentBean> list = new ArrayList<>();
            RecentlyPlayed played = new RecentlyPlayed();
            played.setRecentlyPlayed(list);
            RecentBean recentBean = new RecentBean();
            recentBean.setMediaId(addBean.getMediaId());
            recentBean.setTitle(addBean.getTitle());
            recentBean.setArtist(addBean.getArtist());
            if(addBean.getAlbum() != null){
                recentBean.setAlbum(addBean.getAlbum());
            }
            played.getRecentlyPlayed().add(0,recentBean);
            String toJson = gson.toJson(played);
            SPUtils.getInstance().put("recent",toJson);
        }
    }

    /*
    * 添加最近播放列表
    * */
    public static void addToRecent(MediaBrowserCompat.MediaItem mediaItem){

        String recent = SPUtils.getInstance().getString("recent");
        Gson gson = new Gson();
        RecentlyPlayed recentlyPlayed = gson.fromJson(recent, RecentlyPlayed.class);
        if(recentlyPlayed != null && recentlyPlayed.getRecentlyPlayed() != null && recentlyPlayed.getRecentlyPlayed().size() > 0){
            boolean checkMediaId = checkMediaId(mediaItem.getMediaId(), recentlyPlayed.getRecentlyPlayed());
            if(!checkMediaId){
                RecentBean recentBean = new RecentBean();
                recentBean.setMediaId(mediaItem.getMediaId());
                recentBean.setTitle(mediaItem.getDescription().getTitle().toString());
                recentBean.setArtist(mediaItem.getDescription().getSubtitle().toString());
                if(mediaItem.getDescription().getIconUri() != null){
                    recentBean.setAlbum(mediaItem.getDescription().getIconUri().toString());
                }
                recentlyPlayed.getRecentlyPlayed().add(0,recentBean);
            }
            String toJson = gson.toJson(recentlyPlayed);
            SPUtils.getInstance().put("recent",toJson);
        }else {
            List<RecentBean> list = new ArrayList<>();
            RecentlyPlayed played = new RecentlyPlayed();
            played.setRecentlyPlayed(list);
            RecentBean recentBean = new RecentBean();
            recentBean.setMediaId(mediaItem.getMediaId());
            recentBean.setTitle(mediaItem.getDescription().getTitle().toString());
            recentBean.setArtist(mediaItem.getDescription().getSubtitle().toString());
            if(mediaItem.getDescription().getIconUri() != null){
                recentBean.setAlbum(mediaItem.getDescription().getIconUri().toString());
            }
            played.getRecentlyPlayed().add(0,recentBean);
            String toJson = gson.toJson(played);
            SPUtils.getInstance().put("recent",toJson);
        }
    }

    /*
    * 清除最近列表
    * */
    public static void clearRecent(){
        SPUtils.getInstance().remove("recent");
    }

    /*
    * 清除我的喜欢
    * */
    public static void clearFavorite(){
        SPUtils.getInstance().remove("favorite");
    }

    private static boolean checkMediaId(String mediaId,List<RecentBean> list){
        for (int i = 0; i < list.size(); i++) {
            RecentBean bean = list.get(i);
            String currentId = bean.getMediaId();
            if(currentId.equals(mediaId)){
                return true;
            }
        }
        return false;
    }

    private static int getMediaIdPosition(String mediaId,List<RecentBean> list){
        for (int i = 0; i < list.size(); i++) {
            RecentBean bean = list.get(i);
            String currentId = bean.getMediaId();
            if(currentId.equals(mediaId)){
                return i;
            }
        }
        return -1;
    }
}
