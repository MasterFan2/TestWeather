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
import com.way.net.bean.Resp;
import com.way.net.bean.TwitterDetailResp;
import com.way.net.bean.TwitterInfo;
import com.way.net.bean.TwitterMain;
import com.way.net.bean.TwitterMainResp;
import com.way.net.bean.TwitterResp;

import java.lang.reflect.Array;
import java.net.CookieManager;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by Administrator on 2015/11/15.
 */
public class HttpClient {

//    private static final String BASE_URL = "http://116.255.235.119:1280/weatherForecastServer";
    private static final String BASE_URL = "http://139.129.97.79:8080/weatherForecastServer";

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

        //首页数据
        @GET("/index/index")
        void getMainCommentsImages(Callback<TwitterMainResp> cb);

        // 获取所有评论
        @GET("/comment/index")
        void getComments(@Query("pageIndex")int pageIndex, @Query("pageSize")int pageSize, Callback<CommentsResult> cb);

        //【new】 发布图片/评论
        @POST("/twitter/save")
        @Multipart
        void sendImageAndComments(@Part("uploadImg") TypedFile file, @Query("mapId")String mapId, @Query("content")String content, @Query("imie")String imie, Callback<Resp> callback);

        //发送评论
        @FormUrlEncoded
        @POST("/comment/save")
        void sendComments(@Field("content")String content, Callback<BaseEntity> cb);

        //【new】 获取微博列表
        @GET("/twitter/index")
        void twitterList(@Query("pageIndex")int pageIndex, @Query("pageSize")int pageSize, Callback<TwitterResp> cb);

        //【new】 赞微博
        @GET("/twitter/support")
        void twitterSupport(@Query("id")int id,  Callback<Resp> cb);

        //【new】 取消赞
        @GET("/twitter/cancelsupport")
        void twitterCancelSupport(@Query("id")int id, Callback<Resp> cb);

        //【new】对微博评论
        @POST("/twitterComment/save")
        void commentTwitter(@Query("twitterId")int twitterId, @Query("content")String content,  @Query("imie")String imie, Callback<Resp> cb);

        //【new】 对微博评论的赞
        @GET("/twitterComment/support")
        void twitterCommentSupport(@Query("id")int id, Callback<Resp> cb);

        //【new】 微博详情
        @GET("/twitter/detail")
        void twitterDetail(@Query("id")int id, Callback<TwitterDetailResp> cb);


         //获取图片列表
        @GET("/img/index")
        void getImages(@Query("pageIndex")int pageIndex, @Query("pageSize")int pageSize, Callback<ImageResult> cb);

         //赞图片
        @GET("/img/support")
        void goodImage(@Query("imgId")int imageId, Callback<BaseEntity> cb);

         //赞评论
        @GET("/comment/support")
        void goodComments(@Query("commentId")int commentsId, Callback<BaseEntity> cb);
    }

    /**
     * 首页数据
     * @param cb
     */
    public void getMainCommentsImages(Callback<TwitterMainResp> cb){
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
     * 发布图片/评论
     * @param file
     * @param mapId
     * @param content
     * @param imie
     */
    public void sendImageAndComments(TypedFile file,String mapId, String content,String imie, Callback<Resp> callback){
        netInterface.sendImageAndComments(file, mapId, content, imie, callback);
    }

    /**
     * 【new】 获取微博列表
     * @param pageIndex
     * @param pageSize
     * @param cb
     */
    public void twitterList(@Query("pageIndex")int pageIndex, @Query("pageSize")int pageSize, Callback<TwitterResp> cb){
        netInterface.twitterList(pageIndex, pageSize, cb);
    }

    /**
     * 【new】 赞
     * @param id
     * @param cb
     */
    public void twitterSupport(int id,  Callback<Resp> cb){
        netInterface.twitterSupport(id, cb);
    }

    /**
     * 【new】 取消赞
     * @param id
     * @param cb
     */
    public void twitterCancelSupport(int id, Callback<Resp> cb){
        netInterface.twitterCancelSupport(id, cb);
    }


    /**
     * 【new】对微博评论
     *
     * @param twitterId
     * @param content
     * @param cb
     */
    public void commentTwitter(int twitterId, String content, String imie, Callback<Resp> cb){
        netInterface.commentTwitter(twitterId, content, imie, cb);
    }

    /**
     * 【new】 对微博评论的赞
     * @param id
     * @param cb
     */
    public void twitterCommentSupport(int id, Callback<Resp> cb){
        netInterface.twitterCommentSupport(id, cb);
    }

    /**
     * 【new】 微博详情
     */

    public void twitterDetail(int id, Callback<TwitterDetailResp> cb){
        netInterface.twitterDetail(id, cb);
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
