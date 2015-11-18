package com.way.net;

import android.content.Context;
import android.telecom.Call;
import android.text.TextUtils;

import com.squareup.okhttp.OkHttpClient;
import com.way.beans.BaseEntity;
import com.way.beans.Comments;
import com.way.beans.CommentsResult;
import com.way.beans.ImageResult;
import com.way.beans.MainPictureComment;
import com.way.common.util.PreferenceUtils;

import java.net.CookieManager;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Administrator on 2015/11/15.
 */
public class HttpClient {

    private static final String BASE_URL = "http://116.255.235.119:1280/weatherForecastServer";

    private Context mContext;
    private RestAdapter restAdapter = null;
    private NetInterface netInterface = null;

    private static HttpClient instanse;

    public HttpClient() {
    }

    public static HttpClient getInstance() {
        if (instanse == null) {
            instanse = new HttpClient();
        }
        return instanse;
    }

    public void init(Context context){
        mContext = context;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setClient(new OkClient(new OkHttpClient().setCookieHandler(new CookieManager())))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {

//                        String temp = PreferenceUtils.getValue(mContext, PreferenceUtils.PREFERENCE_B_TOKEN, PreferenceUtils.DataType.STRING);
//                        if (!TextUtils.isEmpty(temp)) {
//                            // 设置JSESSIONID
//                            request.addHeader("btoken", temp);
//                        }
                    }
                })
                .build();

        netInterface = restAdapter.create(NetInterface.class);
    }

    //接口
    interface NetInterface {

        /**
         * 首页数据
         * @param cb
         */
        @GET("/index/index")
        void getMainCommentsImages(Callback<MainPictureComment> cb);

        /**
         * 获取所有评论
         * @param pageIndex
         * @param pageSize
         * @param cb
         */
        @GET("/comment/index")
        void getComments(@Query("pageIndex")int pageIndex, @Query("pageSize")int pageSize, Callback<CommentsResult> cb);

        /**
         * 发送评论
         * @param content
         * @param cb
         */
        @FormUrlEncoded
        @POST("/comment/save")
        void sendComments(@Field("content")String content, Callback<BaseEntity> cb);

        /**
         * 获取图片列表
         * @param pageIndex
         * @param pageSize
         * @param cb
         */
        @GET("/img/index")
        void getImages(@Query("pageIndex")int pageIndex, @Query("pageSize")int pageSize, Callback<ImageResult> cb);

        /**
         * 赞图片
         * @param imageId
         * @param cb
         */
        @GET("/img/support")
        void goodImage(@Query("imgId")int imageId, Callback<BaseEntity> cb);

        /**
         * 赞评论
         * @param commentsId
         * @param cb
         */
        @GET("/comment/support")
        void goodComments(@Query("commentId")int commentsId, Callback<BaseEntity> cb);
    }

    /**
     * 首页数据
     * @param cb
     */
    public void getMainCommentsImages(Callback<MainPictureComment> cb){
        netInterface.getMainCommentsImages(cb);
    }

    /**
     * 获取所有评论
     * @param pageIndex
     * @param pageSize
     * @param cb
     */
    public void getComments(int pageIndex, int pageSize, Callback<CommentsResult> cb){
        netInterface.getComments(pageIndex, pageSize, cb);
    }


    /**
     * 发送评论
     * @param content
     * @param cb
     */
    public void sendComments(String content, Callback<BaseEntity> cb){
        netInterface.sendComments(content, cb);
    }

    /**
     * 获取图片列表
     * @param pageIndex
     * @param pageSize
     * @param cb
     */
    public void getImages(int pageIndex, int pageSize, Callback<ImageResult> cb){
        netInterface.getImages(pageIndex, pageSize, cb);
    }

    /**
     * 赞图片
     * @param imageId
     * @param cb
     */
    public  void goodImage(int imageId, Callback<BaseEntity> cb){
        netInterface.goodImage(imageId, cb);
    }

    /**
     * 赞评论
     * @param commentsId
     * @param cb
     */
    public  void goodComments(int commentsId, Callback<BaseEntity> cb){
        netInterface.goodComments(commentsId, cb);
    }
}