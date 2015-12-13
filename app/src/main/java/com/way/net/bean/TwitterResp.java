package com.way.net.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/12.
 */
public class TwitterResp {

    private ArrayList<TwitterInfo> result;

    public ArrayList<TwitterInfo> getResult() {
        return result;
    }

    public void setResult(ArrayList<TwitterInfo> result) {
        this.result = result;
    }

    public TwitterResp(ArrayList<TwitterInfo> result) {

        this.result = result;
    }
}
