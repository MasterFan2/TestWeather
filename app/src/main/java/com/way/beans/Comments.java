package com.way.beans;

/**
 * 评论
 * Created by 13510 on 2015/11/9.
 */
public class Comments {

    private int id;
    private String content;
    private int supportNum;

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

    public int getSupportNum() {
        return supportNum;
    }

    public void setSupportNum(int supportNum) {
        this.supportNum = supportNum;
    }

    public Comments(int id, String content, int supportNum) {

        this.id = id;
        this.content = content;
        this.supportNum = supportNum;
    }
}
