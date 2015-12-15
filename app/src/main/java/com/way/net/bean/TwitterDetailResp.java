package com.way.net.bean;

/**
 * Created by Administrator on 2015/12/13.
 */
public class TwitterDetailResp {

    private TwitterInfo result;

    public TwitterInfo getResult() {
        return result;
    }

    public void setResult(TwitterInfo result) {
        this.result = result;
    }

    public TwitterDetailResp(TwitterInfo result) {

        this.result = result;
    }
}
