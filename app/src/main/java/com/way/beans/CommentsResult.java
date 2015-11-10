package com.way.beans;

import java.util.List;

/**
 * Created by Administrator on 2015/11/9.
 */
public class CommentsResult {
    List<Comments> result;

    public CommentsResult(List<Comments> result) {
        this.result = result;
    }

    public List<Comments> getResult() {
        return result;
    }

    public void setResult(List<Comments> result) {
        this.result = result;
    }
}
