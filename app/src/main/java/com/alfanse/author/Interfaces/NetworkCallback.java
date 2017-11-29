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
