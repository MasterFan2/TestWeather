package com.way.net.bean;

/**
 * Created by Administrator on 2015/12/11.
 */
public class Resp {

    private String msg;
    private int code;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Resp(String msg, int code) {

        this.msg = msg;
        this.code = code;
    }
}
