/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Velocity-1601 on 6/4/2017.
 */

public class Author implements Serializable {

    private String id;
    private String firebaseId;
    private String name;
    private String gender;
    private String dob;
    private String mobile;
    private String email;
    private String profileImage;
    private String coverImage;
    private String status;
    private String totalQuotes;
    private String totalLikes;
    private String totalFollowers;
    private String totalFollowing;
    private String dateCreated;
    private Country country;
    private Boolean followingAuthor;
    private ArrayList<Quote> quotes;
    private ArrayList<Author> followers;
    private ArrayList<Author> following;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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

    public String getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(String totalLikes) {
        this.totalLikes = totalLikes;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public ArrayList<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(ArrayList<Quote> quotes) {
        this.quotes = quotes;
    }

    public ArrayList<Author> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<Author> followers) {
        this.followers = followers;
    }

    public Boolean isFollowingAuthor() {
        return followingAuthor;
    }

    public void setFollowingAuthor(Boolean followingAuthor) {
        this.followingAuthor = followingAuthor;
    }

    public ArrayList<Author> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<Author> following) {
        this.following = following;
    }

}
