package com.alfanse.author.Models;

/**
 * Created by Velocity-1601 on 6/21/2017.
 */

public class Comment {

    private Author author;
    private String comment;
    private String totalLikes;
    private Boolean likeComment;
    private String dateAdded;
    private Boolean isAbusive;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean isLikeComment() {
        return likeComment;
    }

    public void setLikeComment(Boolean likeComment) {
        this.likeComment = likeComment;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Boolean isAbusive() {
        return isAbusive;
    }

    public void setAbusive(Boolean abusive) {
        isAbusive = abusive;
    }

    public String getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(String totalLikes) {
        this.totalLikes = totalLikes;
    }
}
