package com.ckw.lightweightmusicplayer.NetLoader;


public interface CallBackListener<T> {

    /**
     * 成功的回调
     *
     * @param data 业务数据
     */
    void onSuccess(T data);

    /**
     * 失败的回调
     *
     * @param errorMsg 显示的错误信息
     */
    void onError(String errorMsg);
}
