package com.alfanse.author.CustomViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alfanse.author.R;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class ComponentBoxView extends ComponentView {

    private Context mContext;
    private QuoteCanvas mCanvas;
    private ImageView mImageView;
    private int mColor;

    public ComponentBoxView(@NonNull Context context, @NonNull QuoteCanvas canvas) {
        super(context, canvas);
        super.setResizable(true);
        mContext = context;
        mCanvas = canvas;
        mImageView = new ImageView(mContext);
        Draw();
    }

    private void Draw() {

        setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        addView(mImageView);

        super.mImgRemove.bringToFront();
        super.mImgResize.bringToFront();
    }

    public int getBackgroundColor() {
        return mColor;
    }

    public void setBackgroundColor(int color) {
        mColor = color;
        mImageView.setBackgroundColor(color);
    }
}
