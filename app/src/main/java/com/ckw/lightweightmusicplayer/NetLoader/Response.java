package com.ckw.lightweightmusicplayer.NetLoader;

/**
 * Created by ckw
 * on 2017/12/12.
 * 根据具体的业务需求来写
 */

public class Response<T>{
    private int code;  //状态码  0：失败  1：成功
    private String msg; // 显示的信息
    private T data; // 业务数据

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
