package com.way.beans;

import java.util.List;

/**
 * Created by Administrator on 2015/11/9.
 */
public class ImageResult {

    List<Image> result;

    public ImageResult(List<Image> result) {
        this.result = result;
    }

    public List<Image> getResult() {
        return result;
    }

    public void setResult(List<Image> result) {
        this.result = result;
    }
}
