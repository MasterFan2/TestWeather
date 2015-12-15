package com.way.yahoo;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.way.common.util.SystemUtils;
import com.way.net.HttpClient;

import org.xutils.x;

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

    @Override
    public void onCreate() {

        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志

        FIR.init(this);
        super.onCreate();
        mApplication = this;
        SystemUtils.copyDB(this);// 程序第一次运行将数据库copy过去
        HttpClient.getInstance().init(this);
    }
}
