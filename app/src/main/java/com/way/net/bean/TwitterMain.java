package com.way.net.bean;

/**
 * Created by Administrator on 2015/12/12.
 */
public class TwitterMain {

    private int id;
    private int commentNum;
    private String content;
    private String dateCreated;
    private String imgs;
    private String lastUpdated;
    private int supportNum;

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
