package com.way.yahoo;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
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
}
