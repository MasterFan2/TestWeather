package com.way.beans;

/**
 * Created by Administrator on 2015/11/11.
 */
public class UploadResp {
    private int code;
    private String imgName;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public UploadResp(int code, String imgName) {

        this.code = code;
        this.imgName = imgName;
    }
}
