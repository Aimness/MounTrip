package com.company.android.myapplication.Search;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Reply {
    private String uid;
    private String comment;
    private Long commentTime;
    private String commentId;

    public Reply(String uid, String comment, Long commentTime, String commentId)
    {
        this.comment = comment;
        this.uid = uid;
        this.commentTime = commentTime;
        this.commentId = commentId;
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

    public String getCommentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
        Date date = new Date(commentTime);
        return simpleDateFormat.format(date);
    }

    public void setCommentTime(Long commentTime) {
        this.commentTime = commentTime;
    }

    public Reply()
    {

    }

}
