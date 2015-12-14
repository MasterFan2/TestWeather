package com.way.net.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2015/12/12.
 */
@Table(name = "CommentLikeStatus")
public class CommentLikeStatus {

    @Column(name = "id", isId = true)
    private int id ;

    @Column(name = "twitterId")
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
