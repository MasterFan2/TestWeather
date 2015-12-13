package com.way.net.bean;

/**
 * Created by Administrator on 2015/12/12.
 */
public class TwitterMainResp {
    private TwitterMain topTwitter;

    public TwitterMain getTopTwitter() {
        return topTwitter;
    }

    public void setTopTwitter(TwitterMain topTwitter) {
        this.topTwitter = topTwitter;
    }

    public TwitterMainResp(TwitterMain topTwitter) {

        this.topTwitter = topTwitter;
    }
}
