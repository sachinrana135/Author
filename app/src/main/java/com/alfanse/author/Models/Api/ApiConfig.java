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
