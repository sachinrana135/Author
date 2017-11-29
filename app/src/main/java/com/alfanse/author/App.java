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

package com.alfanse.author;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.alfanse.author.Utilities.FontHelper;
import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Velocity-1601 on 6/20/2017.
 */

public class App extends Application {
    private static App mInstance;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);// to support drawable left or similar in pre-lollipop devices
    }

    private Context mContext;

    public static synchronized App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        mInstance = this;
        mContext = getApplicationContext();
        FontHelper.getInstance(mContext).overrideFont();
    }

}
