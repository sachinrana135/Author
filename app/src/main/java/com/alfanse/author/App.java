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
