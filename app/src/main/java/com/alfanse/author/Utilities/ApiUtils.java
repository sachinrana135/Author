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

package com.alfanse.author.Utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.alfanse.author.BuildConfig;
import com.alfanse.author.Interfaces.NetworkCallback;

import java.util.HashMap;


/**
 * Created by Velocity-1601 on 7/1/2017.
 */

public class ApiUtils {

    private Activity activity;
    private Context context;
    private String message;
    private String url;
    private Boolean shouldCache = false;
    private Boolean showError = true;
    private HashMap<String, String> headerParams;
    private HashMap<String, String> params;
    private NetworkCallback.stringResponseCallback stringResponseCallback;
    private NetworkCallback.jsonResponseCallback jsonResponseCallback;

    public ApiUtils(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public Activity getActivity() {
        return activity;
    }

    public ApiUtils setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ApiUtils setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ApiUtils setUrl(String url) {
        this.url = url;
        return this;
    }

    public Boolean shouldCache() {
        return shouldCache;
    }

    public ApiUtils setShouldCache(Boolean shouldCache) {
        this.shouldCache = shouldCache;
        return this;
    }

    public Boolean showError() {
        return showError;
    }

    public ApiUtils setShowError(Boolean showError) {
        this.showError = showError;
        return this;
    }

    public HashMap<String, String> getHeaderParams() {
        return headerParams;
    }

    public ApiUtils setHeaderParams(HashMap<String, String> headerParams) {
        this.headerParams = headerParams;
        return this;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public ApiUtils setParams(HashMap<String, String> params) {
        this.params = params;
        return this;
    }

    public NetworkCallback.stringResponseCallback getStringResponseCallback() {
        return stringResponseCallback;
    }

    public ApiUtils setStringResponseCallback(NetworkCallback.stringResponseCallback stringResponseCallback) {
        this.stringResponseCallback = stringResponseCallback;
        return this;
    }

    public NetworkCallback.jsonResponseCallback getJsonResponseCallback() {
        return jsonResponseCallback;
    }

    public ApiUtils setJsonResponseCallback(NetworkCallback.jsonResponseCallback jsonResponseCallback) {
        this.jsonResponseCallback = jsonResponseCallback;
        return this;
    }

    public void call() {

        final String correlationID = Utils.getRandomString();
        final String deviceId = Utils.getDeviceId(context);

        HashMap<String, String> headerParam = new HashMap<>();
        headerParam.put(Constants.API_HEADER_PARAM_KEY_CORRELATION_ID, correlationID);
        headerParam.put(Constants.API_HEADER_PARAM_KEY_DEVICE_ID, deviceId);
        headerParam.put(Constants.API_HEADER_PARAM_KEY_CALL_SOURCE, getMessage());
        headerParam.put(Constants.API_HEADER_PARAM_KEY_APP_VERSION_CODE, Integer.toString(Utils.getInstance(context).getAppVersionCode()));
        headerParam.put(Constants.API_HEADER_PARAM_KEY_APP_VERSION_NAME, Utils.getInstance(context).getAppVersionName());
        headerParam.put(Constants.API_HEADER_PARAM_KEY_API_TOKEN, Constants.API_TOKEN);

        if (this.getHeaderParams() != null) {
            headerParam.putAll(this.getHeaderParams());
        }

        this.setHeaderParams(headerParam);

        HashMap<String, String> requestParam = new HashMap<>();
        if (SharedManagement.getInstance(context).getString(SharedManagement.LOGGED_USER) != null) {
            requestParam.put(Constants.API_PARAM_KEY_LOGGED_AUTHOR_ID, SharedManagement.getInstance(context).getLoggedUser().getId());
        }

        if (this.getParams() != null) {
            requestParam.putAll(this.getParams());
        }
        this.setParams(requestParam);

        try {
            if (BuildConfig.DEBUG) {
                String message = getMessage();
                message = message + "\t" + getUrl();
                message = message + "\t" + correlationID;
                message = message + "\t" + "REQUEST_PARAM";
                message = message + "\t" + getParams().toString();
                message = message + "\t" + "HEADER_PARAM";
                message = message + "\t" + getHeaderParams().toString();
                Log.d("API Request", message);
                Utils.logInfo(message);
            }
        } catch (Exception e) {
            Utils.logException(e);
        }
        NetworkUtils.getInstance(context).sendStringRequest(this);

    }
}
