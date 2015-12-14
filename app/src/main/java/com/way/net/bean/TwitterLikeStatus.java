package com.way.net.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2015/12/12.
 */
@Table(name = "TwitterLikeStatus")
public class TwitterLikeStatus {

    @Column(name = "id", isId = true)
    private int id ;

    @Column(name = "twitterId")
    private int twitterId;

    @Column(name = "imie")
    private String imie;

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

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public TwitterLikeStatus() {
    }

    public TwitterLikeStatus(int twitterId, String imie) {

        this.twitterId = twitterId;
        this.imie = imie;
    }
}
