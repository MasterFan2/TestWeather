package com.way.beans;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2015/11/11.
 */
@Table(name = "GoodImageComment")
public class GoodImageComment {

    private int id;

    @Column(column = "tag")
    private String tag;//image or comment

    @Column(column = "itemId")
    private int itemId;

    @Column(column = "date")
    private String date;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public GoodImageComment() {}

    public GoodImageComment(String tag, int itemId, String date) {

        this.tag = tag;
        this.itemId = itemId;
        this.date = date;
    }
}
