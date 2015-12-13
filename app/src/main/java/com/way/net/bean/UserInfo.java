package com.way.net.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/11.
 */
public class UserInfo implements Serializable{
    private int id;
    private String dateCreated;
    private String imie;
    private String lastUpdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public UserInfo(int id, String dateCreated, String imie, String lastUpdated) {

        this.id = id;
        this.dateCreated = dateCreated;
        this.imie = imie;
        this.lastUpdated = lastUpdated;
    }
}
