package com.alfanse.author.Models;

import com.google.gson.JsonElement;

/**
 * Created by Velocity-1601 on 6/15/2017.
 */

public class ApiResponse {
    private JsonElement api;
    private JsonElement data;
    private JsonElement error;

    public JsonElement getApi() {
        return api;
    }

    public void setApi(JsonElement api) {
        this.api = api;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public JsonElement getError() {
        return error;
    }

    public void setError(JsonElement error) {
        this.error = error;
    }
}


