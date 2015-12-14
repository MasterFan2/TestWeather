package com.way.beans;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2015/11/11.
 */
@Table(name = "GoodImageComment")
public class GoodImageComment {

    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "tag")
    private String tag;//image or comment

    @Column(name = "itemId")
    private int itemId;

    @Column(name = "date")
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
