/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Interfaces;


/**
 * Created by Velocity-1601 on 7/1/2017.
 */

import org.json.JSONObject;

/**
 * interface that is used to declare the callbacks for the network request calls whether the API
 * call was successful or not
 */

public class NetworkCallback {

    public interface stringResponseCallback {
        /**
         * Declaration of the function of the interface for the successful callback of the network
         * request made in the application
         *
         * @param stringResponse the string response that was returned from the API request and will be parsed
         *                       in the application
         */
        void onSuccessCallBack(String stringResponse);

        /**
         * Declaration of the function of the callback interface for the errorneous response returned
         * by the API calls
         *
         * @param e the type of the networkError will was returned due to different reasons
         *          such as internet connectivity issue, server not responding etc.
         */
        void onFailureCallBack(Exception e);
    }

    public interface jsonResponseCallback {
        /**
         * Declaration of the function of the interface for the successful callback of the network
         * request made in the application
         *
         * @param jsonResponse the json response that was returned from the API request and will be parsed
         *                     in the application
         */
        void onSuccessCallBack(JSONObject jsonResponse);

        /**
         * Declaration of the function of the callback interface for the errorneous response returned
         * by the API calls
         *
         * @param e the type of the networkError will was returned due to different reasons
         *          such as internet connectivity issue, server not responding etc.
         */
        void onFailureCallBack(Exception e);
    }
}
