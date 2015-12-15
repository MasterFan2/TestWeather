package com.way.net.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 保存是否缓存过当前界面， 策略：如果有数据
 * Created by master on 2015-12-14.
 */
@Table(name = "CacheWhere")
public class CacheWhere {

    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "pageName")
    private String pageName; //页面名称

    @Column(name = "date")
    private String date;     //日期

    public CacheWhere() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public CacheWhere(int id, String pageName, String date) {
        this.id = id;
        this.pageName = pageName;
        this.date = date;
    }
}
