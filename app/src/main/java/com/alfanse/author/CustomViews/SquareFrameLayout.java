package com.alfanse.author.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class SquareFrameLayout extends FrameLayout {

    private Context mContext;
    private ImageView mImageView;

    public SquareFrameLayout(Context context) {
        super(context);
        mContext = context;
        mImageView = new ImageView(mContext);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mImageView = new ImageView(mContext);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        mImageView = new ImageView(mContext);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSize == 0 && heightSize == 0) {
            // If there are no constraints on size, let FrameLayout measure
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Now use the smallest of the measured dimensions for both dimensions
            final int minSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
            setMeasuredDimension(minSize, minSize);
            return;
        }

        final int size;
        if (widthSize == 0 || heightSize == 0) {
            // If one of the dimensions has no restriction on size, set both dimensions to be the
            // on that does
            size = Math.max(widthSize, heightSize);
        } else {
            // Both dimensions have restrictions on size, set both dimensions to be the
            // smallest of the two
            size = Math.min(widthSize, heightSize);
        }

        final int newMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(newMeasureSpec, newMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                UnFocusChildren();
                break;
            }
        }
        return true;
    }

    private void UnFocusChildren() {

        for (int index = 0; index < (this).getChildCount(); ++index) {
            View componentView = (this).getChildAt(index);

            if ((componentView instanceof ComponentView)) {

                ((ComponentView) componentView).setStateFocused(false);

            }
        }

    }

    public void setBackgroundImage(Bitmap bitmap) {
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageView.setImageBitmap(bitmap);
        mImageView.setAdjustViewBounds(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        addView(mImageView);
    }

}
