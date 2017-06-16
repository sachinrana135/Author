package com.alfanse.author.Utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.alfanse.author.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class Utils {

    private static Utils sInstance;
    private static Context mContext;

    private Utils(Context context) {
        mContext = context;
    }

    public static synchronized Utils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Utils(context);
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

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return mContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return mContext.getResources().getConfiguration().locale;
        }
    }

    public int getColor(int id) {
        return ContextCompat.getColor(mContext, id);
    }

    public int[] getTagColors() {
        return mContext.getResources().getIntArray(R.array.tagColors);
    }

    public int getRandomTagColor() {
        int[] tagColors = this.getTagColors();
        return tagColors[new Random().nextInt(tagColors.length)];
    }

    public String getJsonResponse(String fileName) {
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
        return json;

        /*JSONObject jsonResponseObject = null;
        try {
            jsonResponseObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResponseObject;*/
    }

    public JSONObject loadJSONFromAsset(String fontsJsonFileName) {

        String json = null;
        try {
            InputStream is = mContext.getAssets().open(fontsJsonFileName);
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

    public String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void hideSoftKeyboard(Activity activity) {

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean hasPermissions(String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext != null && permissions != null) {
            for (String permission : permissions) {
                if (mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
