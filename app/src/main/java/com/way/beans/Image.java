package com.way.beans;

/**
 * 图片
 * Created by 13510 on 2015/11/9.
 */
public class Image {
    private int id;
    private int supportNum;
    private String url;

    public Image(int id, int supportNum, String url) {
        this.id = id;
        this.supportNum = supportNum;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSupportNum() {
        return supportNum;
    }

    public void setSupportNum(int supportNum) {
        this.supportNum = supportNum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
