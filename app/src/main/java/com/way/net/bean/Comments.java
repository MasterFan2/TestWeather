package com.way.net.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Administrator on 2015/12/11.
 */
@Table(name = "Comments")
public class Comments implements Serializable, Comparable<Comments> {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Column(name = "autoId", isId = true)
    private int autoId;

    @Column(name = "id")
    private int id;

    @Column(name = "content")
    private String content;

    @Column(name = "dateCreated")
    private String dateCreated;

    @Column(name = "lastUpdated")
    private String lastUpdated;

    @Column(name = "supportNum")
    private int supportNum;

    @Column(name = "twitter")
    private String twitter;

    @Column(name = "userinfo")
    private String userinfo;

    @Column(name = "isLike")
    private boolean isLike;

    @Column(name = "twitterId")
    private int twitterId;

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    public int getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(int twitterId) {
        this.twitterId = twitterId;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public Comments() {
    }

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

    private Date getBirthday(String strDate) {
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int compareTo(Comments comments) {
        Date d1 = getBirthday(comments.getDateCreated());
        Date d2 = getBirthday(this.getDateCreated());

        if (d1 == null && d2 == null)
            return 0;

        if (d1 == null)
            return -1;

        if (d2 == null)
            return 1;

        return d1.compareTo(d2);
    }
}
