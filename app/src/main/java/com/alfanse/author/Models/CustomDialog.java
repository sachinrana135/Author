/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Models;

import android.app.Activity;

/**
 * Created by Velocity-1601 on 6/15/2017.
 */

public class CustomDialog {

    private Activity activity;
    private int dialogType;
    private String title;
    private String message;
    private Boolean isHtml = false;

    public Activity getActivity() {
        return activity;
    }

    public CustomDialog setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public int getDialogType() {
        return dialogType;
    }

    public CustomDialog setDialogType(int dialogType) {
        this.dialogType = dialogType;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CustomDialog setTitle(String title) {
        this.title = title;
        return this;

    }

    public String getMessage() {
        return message;
    }

    public CustomDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public Boolean isHtml() {
        return isHtml;
    }

    public CustomDialog setHtml(Boolean html) {
        isHtml = html;
        return this;
    }
}
