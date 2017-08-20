package com.alfanse.author;

import android.app.Application;
import android.content.Context;

import com.alfanse.author.Utilities.FontHelper;

/**
 * Created by Velocity-1601 on 6/20/2017.
 */

public class App extends Application {
    private static App mInstance;
    private Context mContext;

    public static synchronized App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
        FontHelper.getInstance(mContext).overrideFont();
    }

}
