package com.alfanse.author.Models;

import java.io.Serializable;

/**
 * Created by Velocity-1601 on 6/4/2017.
 */

public class Author implements Serializable {

    private String authorId;
    private String originalName;
    private String authorName;
    private String originalProfileImage;
    private String authorProfileImage;
    private String email;
    private String authorCoverImage;
    private String status;
    private String totalQuotes;
    private String totalFollowers;
    private String totalFollowing;
    private String dateCreated;
    private String country;
    private Quotes quotes;


    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getOriginalProfileImage() {
        return originalProfileImage;
    }

    public void setOriginalProfileImage(String originalProfileImage) {
        this.originalProfileImage = originalProfileImage;
    }

    public String getAuthorProfileImage() {
        return authorProfileImage;
    }

    public void setAuthorProfileImage(String authorProfileImage) {
        this.authorProfileImage = authorProfileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthorCoverImage() {
        return authorCoverImage;
    }

    public void setAuthorCoverImage(String authorCoverImage) {
        this.authorCoverImage = authorCoverImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalQuotes() {
        return totalQuotes;
    }

    public void setTotalQuotes(String totalQuotes) {
        this.totalQuotes = totalQuotes;
    }

    public String getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(String totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public String getTotalFollowing() {
        return totalFollowing;
    }

    public void setTotalFollowing(String totalFollowing) {
        this.totalFollowing = totalFollowing;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Quotes getQuotes() {
        return quotes;
    }

    public void setQuotes(Quotes quotes) {
        this.quotes = quotes;
    }
}
