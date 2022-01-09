package com.company.android.myapplication;


import com.company.android.myapplication.Search.ReplyAdapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class Comments {
    private String uid;
    private String comment;
    private Long commentTime;
    private String commentId;
    private List<ReplyAdapter> mReplyList;

    public Comments()
    {

    }

    public Comments(String uid, String comment, Long commentTime, String commentId, List<ReplyAdapter>mReplyList)
    {
        this.comment = comment;
        this.uid = uid;
        this.commentTime = commentTime;
        this.commentId = commentId;
        this.mReplyList = mReplyList;
    }

    public Long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Long commentTime) {
        this.commentTime = commentTime;
    }

    public List<ReplyAdapter> getReplyList() {
        return mReplyList;
    }

    public void setReplyList(List<ReplyAdapter> replyList) {
        mReplyList = replyList;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}

