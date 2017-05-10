package com.alfanse.author.CustomViews;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonMethod;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class ComponentTextView extends ComponentView {

    private Context mContext;
    private QuoteCanvas mCanvas;
    private TextView mTextView;
    private String mText;
    private Typeface mTypeface = null;
    private int mStyle = 0;
    private int mGravity;

    public ComponentTextView(@NonNull Context context, @NonNull QuoteCanvas canvas) {
        super(context, canvas);
        super.setResizable(true);

        mCanvas = canvas;
        mContext = context;
        mTextView = new TextView(context);
        mTypeface = mTextView.getTypeface();
        mStyle = mTypeface.getStyle();
        mText = mContext.getString(R.string.text_default_quote_fragment_write_quote_fragment);
        Draw();
    }

    private void Draw() {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        mTextView.setText(mText);

        mTextView.setPadding(
                (int) mContext.getResources().getDimension(R.dimen.spacing_xxsmall),
                (int) mContext.getResources().getDimension(R.dimen.spacing_xxsmall),
                (int) mContext.getResources().getDimension(R.dimen.spacing_xxsmall),
                (int) mContext.getResources().getDimension(R.dimen.spacing_xxsmall));


        this.addView(mTextView, layoutParams);

        super.mImgRemove.bringToFront();
        super.mImgResize.bringToFront();
    }

    public Float getTextSize() {
        Float sizeInPx = mTextView.getTextSize(); // return value in px unit
        return CommonMethod.getInstance(mContext).pixelToSp(sizeInPx);
    }

    public void setTextSize(Float textSize) {
        mTextView.setTextSize(textSize);// Set value in sp unit
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public void setTypeface(Typeface textFontFamily) {
        mTypeface = textFontFamily;
        mTextView.setTypeface(textFontFamily, getTextStyle());
    }

    public int getTextColor() {
        return mTextView.getCurrentTextColor();
    }

    public void setTextColor(int textColor) {
        mTextView.setTextColor(textColor);
    }

    public int getTextStyle() {
        return mStyle;
    }

    public void setTextStyle(int textStyle) {
        mStyle = textStyle;
        mTextView.setTypeface(getTypeface(), textStyle);
    }

    public Float getTextLocationX() {
        return this.getX();
    }

    public void setTextLocationX(Float textLocationX) {
        this.setX(mCanvas.getWidth() * textLocationX / 100);
    }

    public Float getTextLocationY() {
        return this.getY();
    }

    public void setTextLocationY(Float textLocationY) {
        this.setY(mCanvas.getHeight() * textLocationY / 100);
    }

    public int getAlignment() {
        return mGravity;
    }

    public void setAlignment(int gravity) {
        mGravity = gravity;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = gravity;
        mTextView.setLayoutParams(layoutParams);
    }

    public void setTextBold() {

        if (getTextStyle() == Typeface.BOLD_ITALIC) {
            setTextStyle(Typeface.ITALIC);
        } else if (getTextStyle() == Typeface.ITALIC) {
            setTextStyle(Typeface.BOLD_ITALIC);
        } else if (getTextStyle() == Typeface.BOLD) {
            setTextStyle(Typeface.NORMAL);
        } else {
            setTextStyle(Typeface.BOLD);
        }
    }

    public void setTextItalic() {

        if (getTextStyle() == Typeface.BOLD_ITALIC) {
            setTextStyle(Typeface.BOLD);
        } else if (getTextStyle() == Typeface.ITALIC) {
            setTextStyle(Typeface.NORMAL);
        } else if (getTextStyle() == Typeface.BOLD) {
            setTextStyle(Typeface.BOLD_ITALIC);
        } else {
            setTextStyle(Typeface.ITALIC);
        }
    }


    public void setTextUnderline() {

        if ((mTextView.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) > 0) {
            mTextView.setPaintFlags(mTextView.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        } else {
            mTextView.setPaintFlags(mTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    public Boolean hasTextUnderline() {

        return (mTextView.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) > 0;

    }
}
