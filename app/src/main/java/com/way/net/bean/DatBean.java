package com.way.net.bean;

/**
 * Created by Administrator on 2015/12/9.
 */
public class DatBean {

    public static final int ITEM = 0;
    public static final int SECTION = 1;

    private String date;
    private String name;
    private String desc;
    private String addr;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public DatBean(String date, String name, String desc, String addr, int type) {
        this.date = date;
        this.name = name;
        this.desc = desc;
        this.addr = addr;
        this.type = type;
    }
}
