package com.way.yahoo;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.igexin.sdk.PushManager;
import com.way.common.util.SystemUtils;
import com.way.net.HttpClient;
import com.way.receiver.Utils;
import com.way.utils.Conf;

import org.xutils.x;

import java.lang.reflect.Field;

import im.fir.sdk.FIR;

public class MasterApplication extends Application {
    private static MasterApplication mApplication;
    private static RequestQueue mVolleyRequestQueue;

    public static synchronized RequestQueue getVolleyRequestQueue() {
        if (mVolleyRequestQueue == null)
            mVolleyRequestQueue = Volley.newRequestQueue(mApplication);
        return mVolleyRequestQueue;
    }

    public static synchronized MasterApplication getApplication() {
        return mApplication;
    }

    // SDK参数，会自动从Manifest文件中读取，第三方无需修改下列变量，请修改AndroidManifest.xml文件中相应的meta-data信息。
    // 修改方式参见个推SDK文档
    private String appkey = "";
    private String appsecret = "";
    private String appid = "";

    @Override
    public void onCreate() {

        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志

        Conf.statusBar_height = getStatusBarHeight();

        FIR.init(this);
        super.onCreate();
        mApplication = this;
        SystemUtils.copyDB(this);// 程序第一次运行将数据库copy过去
        HttpClient.getInstance().init(this);

        // 从AndroidManifest.xml的meta-data中读取SDK配置信息
        String packageName = getApplicationContext().getPackageName();
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                appid = appInfo.metaData.getString("PUSH_APPID");
                appsecret = appInfo.metaData.getString("PUSH_APPSECRET");
                appkey = (appInfo.metaData.get("PUSH_APPKEY") != null) ? appInfo.metaData.get("PUSH_APPKEY").toString() : null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // SDK初始化，第三方程序启动时，都要进行SDK初始化工作
        Log.i("GetuiSdkDemo", "initializing sdk...");
        PushManager.getInstance().initialize(this.getApplicationContext());
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
