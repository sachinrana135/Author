/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Models.Api;

/**
 * Created by srana on 8/23/2017.
 */

public class ApiConfig {

    private String apiStatus;
    private String minSupportVersion;

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getMinSupportVersion() {
        return minSupportVersion;
    }

    public void setMinSupportVersion(String minSupportVersion) {
        this.minSupportVersion = minSupportVersion;
    }
}
