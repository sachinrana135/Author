package com.alfanse.author.Utilities;

import android.content.Context;

/**
 * Created by Velocity-1601 on 5/7/2017.
 */

public class Dimensions {

    private static Dimensions sInstance;
    private static Context mContext;

    private Dimensions(Context context) {
        mContext = context;
    }

    public static synchronized Dimensions getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Dimensions(context);
        }
        return sInstance;
    }

    public int getDimension(int Id) {

        int dimension = (int) mContext.getResources().getDimension(Id);

        return dimension;


    }
}
