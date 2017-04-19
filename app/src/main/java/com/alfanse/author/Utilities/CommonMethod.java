package com.alfanse.author.Utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class CommonMethod {

    private static CommonMethod sInstance;
    private static Context mContext;

    public static synchronized CommonMethod getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CommonMethod(context);
        }
        return sInstance;
    }

    private CommonMethod(Context context) {
        mContext = context;
    }

    public Drawable getDrawable(int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.LOLLIPOP) {
            return ContextCompat.getDrawable(mContext, id);
        } else {
            return mContext.getResources().getDrawable(id);
        }
    }
}
