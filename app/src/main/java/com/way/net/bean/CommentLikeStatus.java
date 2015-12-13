package com.way.net.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2015/12/12.
 */
@Table(name = "CommentLikeStatus")
public class CommentLikeStatus {

    private int id ;

    @Column(column = "twitterId")
    private int twitterId;

    public CommentLikeStatus(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(int twitterId) {
        this.twitterId = twitterId;
    }

    public CommentLikeStatus(int twitterId) {
        this.twitterId = twitterId;
    }
}
