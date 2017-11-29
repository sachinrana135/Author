/*
 * Copyright (c) 2017. Alfanse Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
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
