package com.alfanse.author;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.alfanse.author.Utilities.FontHelper;
import com.crashlytics.android.Crashlytics;

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
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        mContext = getApplicationContext();
        FontHelper.getInstance(mContext).overrideFont();
    }

}
