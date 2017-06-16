package com.alfanse.author.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Velocity-1601 on 12/29/2016.
 */

public class SharedManagement {

    public static final String PREF_NAME = "AUTHOR_PREFS";
    public final static int PRIVATE_MODE = 0;
    public final static String LAST_LOGIN_EMAIL = "LAST_LOGIN_EMAIL";
    public static Context mContext;
    private static SharedManagement mInstance;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private SharedManagement(Context context) {
        mContext = context;
        sharedpreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedpreferences.edit();
    }

    public static synchronized SharedManagement getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedManagement(context);
        }
        return mInstance;
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedpreferences.getString(key, "");
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return sharedpreferences.getInt(key, 0);
    }

    public void remove(String key) {
        sharedpreferences.edit().remove(key).apply();
    }

    public void removeAll() {
        editor.clear().commit();
    }
}
