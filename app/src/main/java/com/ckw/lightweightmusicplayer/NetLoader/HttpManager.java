package com.ckw.lightweightmusicplayer.NetLoader;



import com.ckw.lightweightmusicplayer.base.BasePresenter;
import com.ckw.lightweightmusicplayer.base.BaseView;
import com.ckw.lightweightmusicplayer.common.Constant;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Created by ckw
 * on 2017/12/12.
 */

public class HttpManager {
    public HttpManager() {
    }

    public <T,K extends BasePresenter> void request (Observable<Response<T>> observable,
                                                     final CompositeDisposable compositeDisposable,
                                                     final BaseView view ,
                                                     final CallBackListener listener){
        if (observable == null || compositeDisposable == null || view == null || listener == null) {
            return;
        }

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<T>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<T> response) {
                        if (response == null || !view.isActive()){
                            return;
                        }
                        int code = response.getCode();
                        T data = response.getData();
                        //TODO 根据具体的业务需求来写
                        switch (code) {
                        /*
                         * 失败
                         */
                            case 0:
                                listener.onError(response.getMsg());
                                break;

                        /*
                         * 成功
                         */
                            case 1:
                                listener.onSuccess(response.getData());
                                break;
                            /**
                             * ................
                             */
                            case 2:


                                break;

                            default:
                                listener.onError(response.getMsg());
                                break;
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        if ( e == null || !view.isActive()) {
                            return;
                        }
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            switch (httpException.code()) {
                            /*
                             * 没有网络
                             */
                                case 504:
                                    listener.onError(Constant.NET_WORK_ERROR);
                                    break;

                                default:
                                    listener.onError(Constant.REQUEST_FAILURE);
                                    break;
                            }
                            return;
                        }
                        if (e instanceof ConnectException) {
                            listener.onError(Constant.CAN_NOT_CONNECT_TO_SERVER);
                            return;
                        }
                        if (e instanceof SocketException) {
                            listener.onError(Constant.NET_WORK_ERROR);
                            return;
                        }
                        if (e instanceof SocketTimeoutException) {
                            listener.onError(Constant.TIME_OUT);
                            return;
                        }
                        if (e instanceof JsonParseException || e instanceof JSONException ||
                                e instanceof MalformedJsonException) {
                            listener.onError(Constant.DATA_PARSE_EXCEPTION);
                            return;
                        }
                        listener.onError(Constant.REQUEST_FAILURE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
