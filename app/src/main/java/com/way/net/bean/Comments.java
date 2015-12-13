package com.way.net.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/11.
 */
public class Comments implements Serializable {

    private int id;
    private String content;
    private String dateCreated;
    private String lastUpdated;
    private int supportNum;
    private String twitter;
    private String userinfo;
    private boolean isLike;

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public Comments(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(String userinfo) {
        this.userinfo = userinfo;
    }

    public Comments(int id, String content, String dateCreated, String lastUpdated, int supportNum, String twitter, String userinfo) {

        this.id = id;
        this.content = content;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
        this.supportNum = supportNum;
        this.twitter = twitter;
        this.userinfo = userinfo;
    }
}
