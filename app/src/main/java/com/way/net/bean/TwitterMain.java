package com.way.net.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2015/12/12.
 */
@Table(name = "TwitterMain")
public class TwitterMain {

    @Column(name = "autoId", isId = true)
    private int autoId;

    @Column(name = "id")
    private int id;

    @Column(name = "commentNum")
    private int commentNum;

    @Column(name = "content")
    private String content;

    @Column(name = "dateCreated")
    private String dateCreated;

    @Column(name = "imgs")
    private String imgs;

    @Column(name = "lastUpdated")
    private String lastUpdated;

    @Column(name = "supportNum")
    private int supportNum;

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getSupportNum() {
        return supportNum;
    }

    public void setSupportNum(int supportNum) {
        this.supportNum = supportNum;
    }

    public TwitterMain(){}

    public TwitterMain(int id, int commentNum, String content, String dateCreated, String imgs, String lastUpdated, int supportNum) {

        this.id = id;
        this.commentNum = commentNum;
        this.content = content;
        this.dateCreated = dateCreated;
        this.imgs = imgs;
        this.lastUpdated = lastUpdated;
        this.supportNum = supportNum;
    }
}
