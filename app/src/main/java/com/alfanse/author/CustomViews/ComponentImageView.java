package com.alfanse.author.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class ComponentImageView extends ComponentView {

    private Context mContext;
    private SquareFrameLayout mCanvas;
    private ImageView mImageView;
    private Bitmap mBitmap;

    public ComponentImageView(@NonNull Context context, @NonNull SquareFrameLayout canvas, Bitmap bitmap) {
        super(context, canvas);
        super.setScalable(true);

        mContext = context;
        mCanvas = canvas;
        mImageView = new ImageView(mContext);
        mBitmap = bitmap;
        Draw();
    }

    private void Draw() {

        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageView.setImageBitmap(mBitmap);
        mImageView.setAdjustViewBounds(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        addView(mImageView);

        super.mImgRemove.bringToFront();
        super.mImgResize.bringToFront();
    }
}
