package com.alfanse.author.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.alfanse.author.Activities.SignInActivity;
import com.alfanse.author.Models.Author;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

/**
 * Created by Velocity-1601 on 12/29/2016.
 */

public class SharedManagement {

    public static final String PREF_NAME = "AUTHOR_PREFS";
    public final static int PRIVATE_MODE = 0;
    public final static String LAST_LOGIN_EMAIL = "LAST_LOGIN_EMAIL";
    public final static String LOGGED_USER = "LOGGED_USER";
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
        return sharedpreferences.getString(key, null);
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

    public Author getLoggedUser() {
        if (getString(LOGGED_USER) != null) {
            return new Gson().fromJson(getString(LOGGED_USER), Author.class);
        } else {
            logoutUser();
        }
        return null;
    }

    public void setLoggedUser(Author loggedUser) {
        setString(LOGGED_USER, new Gson().toJson(loggedUser));
    }

    public void logoutUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        remove(LOGGED_USER);
        Intent signInIntent = new Intent(mContext, SignInActivity.class);
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(signInIntent);
    }
}
