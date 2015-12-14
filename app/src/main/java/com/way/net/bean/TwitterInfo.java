package com.way.net.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/11.
 */
@Table(name = "TwitterInfo")
public class TwitterInfo implements Serializable{

    public static final int ITEM = 0;
    public static final int SECTION = 1;

    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "commentNum")
    private int commentNum;

    private ArrayList<Comments> comments;

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

    private UserInfo user;

    @Column(name = "isLike")
    private boolean isLike;//是否赞过

    @Column(name = "type")
    private int type;

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
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

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public TwitterInfo(int t, String d){
        this.type = t;
        this.dateCreated = d;
    }

    public TwitterInfo() {
    }

    public TwitterInfo(int id, int commentNum, ArrayList<Comments> comments, String content, String dateCreated, String imgs, String lastUpdated, int supportNum, UserInfo user) {

        this.id = id;
        this.commentNum = commentNum;
        this.comments = comments;
        this.content = content;
        this.dateCreated = dateCreated;
        this.imgs = imgs;
        this.lastUpdated = lastUpdated;
        this.supportNum = supportNum;
        this.user = user;
    }
}
