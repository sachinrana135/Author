package com.alfanse.author.Models.Api;

/**
 * Created by srana on 8/23/2017.
 */

public class ApiError {

    public static final String TYPE_TOAST = "toast";
    public static final String TYPE_DIALOG = "dialog";

    private String type;
    private String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
