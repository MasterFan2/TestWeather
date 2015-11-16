package com.way.beans;

/**
 * Created by Administrator on 2015/11/15.
 */
public class BaseEntity {

    private int code;

    public BaseEntity() {
    }

    public BaseEntity(int code) {

        this.code = code;
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
