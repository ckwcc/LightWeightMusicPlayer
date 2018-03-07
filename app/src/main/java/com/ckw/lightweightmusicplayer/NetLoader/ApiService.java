package com.ckw.lightweightmusicplayer.NetLoader;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by ckw
 * on 2017/12/12.
 * 网络请求api接口
 */

public interface ApiService {

    @Multipart
    @POST("**********")
    Observable<Response<String>> updateUserImg(@PartMap Map<String, RequestBody> map,
                                               @Part MultipartBody.Part image);

}
