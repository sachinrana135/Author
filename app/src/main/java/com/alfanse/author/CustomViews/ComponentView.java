package com.alfanse.author.CustomViews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonMethod;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class ComponentView extends FrameLayout {

    public Drawable drawableRemove;
    protected ImageView mImgResize;
    protected ImageView mImgRemove;
    int buffer = 15;
    private Context mContext;
    private QuoteCanvas mCanvas;
    private boolean mIsResizable;
    private boolean mIsScalable;
    private float dX;
    private float dY;
    private int mOriginalWidth;
    private int mOriginalHeight;
    private double mRatio;
    private int mResizeStartX;
    private int mResizeStartY;
    private int mMinimumWidth;
    private int mMaximumWidth;
    private int mMinimumHeight;
    private int mMaximumHeight;
    private onComponentViewInteractionListener mListener;

    public ComponentView(@NonNull Context context, @NonNull QuoteCanvas canvas) {
        super(context);

        mContext = context;
        mCanvas = canvas;

        mListener = (onComponentViewInteractionListener) mContext;

        mImgRemove = new ImageView(mContext);
        mImgResize = new ImageView(mContext);

        drawableRemove = CommonMethod.getInstance(mContext).getDrawable(R.drawable.ic_resize_grey_24dp);

        mMinimumWidth = getMinimumWidth();
        mMinimumHeight = getMinimumHeight();
        mMaximumWidth = getMaximumWidth();
        mMaximumHeight = getMaximumHeight();

        drawControlButtons();
        initControlButtonListener();
        this.setStateFocused(false);
    }

    private void drawControlButtons() {


        Drawable drawableRemove = CommonMethod.getInstance(mContext).getDrawable(R.drawable.ic_delete_grey_24dp);
        mImgRemove.setImageDrawable(drawableRemove);

        FrameLayout.LayoutParams imgRemoveLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        imgRemoveLayoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        mImgRemove.setLayoutParams(imgRemoveLayoutParams);
        this.addView(mImgRemove);


        Drawable drawableResize = CommonMethod.getInstance(mContext).getDrawable(R.drawable.ic_resize_grey_24dp);
        mImgResize.setImageDrawable(drawableResize);

        FrameLayout.LayoutParams imgResizeLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        imgResizeLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        mImgResize.setLayoutParams(imgResizeLayoutParams);
        this.addView(mImgResize);
    }

    private void initControlButtonListener() {

        mImgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvas.removeView(ComponentView.this);
                if (mListener != null) {
                    mListener.onRemoveButtonClick();
                }

            }
        });

        mImgResize.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent ev) {
                ResizeTouchEvent(ev);
                return true;
            }
        });
    }

    public void setStateFocused(boolean isFocus) {

        if (isFocus) {
            unFocusOtherViews();
            this.setBackgroundDrawable(CommonMethod.getInstance(mContext).getDrawable(R.drawable.border));
            mImgRemove.setVisibility(VISIBLE);
            mImgResize.setVisibility(VISIBLE);
        } else {
            this.setBackgroundDrawable(null);
            mImgRemove.setVisibility(INVISIBLE);
            mImgResize.setVisibility(INVISIBLE);
        }
    }

    private void unFocusOtherViews()

    {
        for (int index = 0; index < (mCanvas).getChildCount(); ++index) {
            View componentView = (mCanvas).getChildAt(index);

            if ((componentView instanceof ComponentView) && (componentView != this)) {

                ((ComponentView) componentView).setStateFocused(false);
            }
        }
    }

    public boolean isResizable() {
        return mIsResizable;
    }

    public void setResizable(boolean resizable) {
        mIsResizable = resizable;
    }

    public boolean isScalable() {
        return mIsScalable;
    }

    public void setScalable(boolean scalable) {
        mIsScalable = scalable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        DragTouchEvent(ev);
        return true;
    }

    private void DragTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                setStatePressed(true);
                dX = getX() - ev.getRawX();
                dY = getY() - ev.getRawY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                this.animate()
                        .x(ev.getRawX() + dX)
                        .y(ev.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;
            }

            case MotionEvent.ACTION_UP: {
                setStatePressed(false);
                setStateFocused(true);
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                setStatePressed(false);
                setStateFocused(false);
                break;

            }
        }
    }

    private void ResizeTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {

            case MotionEvent.ACTION_DOWN: {

                mOriginalWidth = ComponentView.this.getWidth();
                mOriginalHeight = ComponentView.this.getHeight();

                mResizeStartX = (int) ev.getRawX();
                mResizeStartY = (int) ev.getRawY();

                mRatio = ((double) mOriginalWidth) / ((double) mOriginalHeight);

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = ev.getPointerId(pointerIndex);

                int dX = (int) ev.getRawX() - mResizeStartX;
                int dY = (int) ev.getRawY() - mResizeStartY;

                if (isResizable()) {

                    int newWidth = mOriginalWidth + dX;

                    if (newWidth <= mMinimumWidth) {
                        newWidth = mMinimumWidth;
                    } else if (newWidth >= mMaximumWidth) {
                        newWidth = mMaximumWidth;
                    }

                    int newHeight = mOriginalHeight + dY;

                    if (newHeight <= mMinimumHeight) {
                        newHeight = mMinimumHeight;
                    } else if (newHeight >= mMaximumHeight) {
                        newHeight = mMaximumHeight;
                    }

                    ComponentView.this.getLayoutParams().width = newWidth;
                    ComponentView.this.getLayoutParams().height = newHeight;

                } else if (isScalable()) {

                    dX = (int) (dY * mRatio);

                    if (mRatio > 1) { // Width greater than height
                        mMaximumHeight = (int) (mMaximumWidth / mRatio);
                        mMinimumHeight = (int) (mMinimumWidth / mRatio);
                    } else if (mRatio < 1) { // height greater than width
                        mMaximumWidth = (int) (mMaximumHeight * mRatio);
                        mMinimumWidth = (int) (mMinimumHeight * mRatio);
                    }

                    int newWidth = mOriginalWidth + dX;

                    if (newWidth <= mMinimumWidth) {
                        newWidth = mMinimumWidth;
                    } else if (newWidth >= mMaximumWidth) {
                        newWidth = mMaximumWidth;
                    }

                    int newHeight = mOriginalHeight + dY;

                    if (newHeight <= mMinimumHeight) {
                        newHeight = mMinimumHeight;
                    } else if (newHeight >= mMaximumHeight) {
                        newHeight = mMaximumHeight;
                    }

                    ComponentView.this.getLayoutParams().width = newWidth;
                    ComponentView.this.getLayoutParams().height = newHeight;
                }

                ComponentView.this.requestLayout();
                break;
            }

            case MotionEvent.ACTION_UP: {
                setStatePressed(false);
                setStateFocused(true);
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                setStatePressed(false);
                setStateFocused(false);
                break;

            }
        }
    }

    public void setStatePressed(boolean isPressed) {
        if (isPressed) {
            this.setForeground(CommonMethod.getInstance(mContext).getDrawable(R.drawable.border_with_background));
            mImgResize.setVisibility(VISIBLE);
            mImgRemove.setVisibility(VISIBLE);
        } else {
            this.setForeground(null);
        }
    }

    public int getMinimumWidth() {
        return (drawableRemove.getMinimumWidth() * 2) + buffer;
    }

    public int getMinimumHeight() {
        return (drawableRemove.getMinimumHeight() * 2) + buffer;
    }

    public int getMaximumWidth() {
        return mCanvas.getWidth();
    }

    public int getMaximumHeight() {
        return mCanvas.getHeight();
    }

    public interface onComponentViewInteractionListener {
        void onRemoveButtonClick();
    }

}
