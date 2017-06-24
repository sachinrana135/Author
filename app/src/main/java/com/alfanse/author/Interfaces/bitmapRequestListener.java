package com.alfanse.author.Interfaces;

import android.graphics.Bitmap;

/**
 * Created by Velocity-1601 on 6/24/2017.
 */

public interface bitmapRequestListener {
    void onSuccess(Bitmap bitmap);

    void onError(Exception e);
}
