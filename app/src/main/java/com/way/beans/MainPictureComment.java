package com.way.beans;

/**
 * Created by 13510 on 2015/11/9.
 */
public class MainPictureComment {

    private Image topImg;
    private Comments topComment;

    public Image getTopImg() {
        return topImg;
    }

    public void setTopImg(Image topImg) {
        this.topImg = topImg;
    }

    public Comments getTopComment() {
        return topComment;
    }

    public void setTopComment(Comments topComment) {
        this.topComment = topComment;
    }

    public MainPictureComment(Image topImg, Comments topComment) {

        this.topImg = topImg;
        this.topComment = topComment;
    }
}
