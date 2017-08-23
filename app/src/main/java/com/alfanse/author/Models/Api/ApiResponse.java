package com.alfanse.author.Models.Api;

import com.google.gson.JsonElement;

/**
 * Created by Velocity-1601 on 6/15/2017.
 */

public class ApiResponse {
    private ApiConfig config;
    private JsonElement response;
    private ApiError error;

    public ApiConfig getConfig() {
        return config;
    }

    public void setConfig(ApiConfig config) {
        this.config = config;
    }

    public JsonElement getResponse() {
        return response;
    }

    public void setResponse(JsonElement response) {
        this.response = response;
    }

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }
}


