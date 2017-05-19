package com.alfanse.author.Utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class CommonMethod {

    private static CommonMethod sInstance;
    private static Context mContext;

    private CommonMethod(Context context) {
        mContext = context;
    }

    public static synchronized CommonMethod getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CommonMethod(context);
        }
        return sInstance;
    }

    public static String getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    public Drawable getDrawable(int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.LOLLIPOP) {
            return ContextCompat.getDrawable(mContext, id);
        } else {
            return mContext.getResources().getDrawable(id);
        }
    }

    public Float pixelToSp(Float px) {
        float scaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public JSONObject loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonResponseObject = null;
        try {
            jsonResponseObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResponseObject;
    }
}
