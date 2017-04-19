package com.alfanse.author.CustomViews;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alfanse.author.R;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class ComponentTextView extends ComponentView {

    private Context mContext;
    private SquareFrameLayout mCanvas;
    private TextView mTextView;
    private String mText;

    public ComponentTextView(@NonNull Context context, @NonNull SquareFrameLayout canvas, String text) {
        super(context, canvas);
        super.setResizable(true);

        mCanvas = canvas;
        mContext = context;
        mTextView = new TextView(context);
        mText = text;
        Draw();
    }

    private void Draw() {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        mTextView.setText(mText);

        mTextView.setPadding(50, 50, 50, 50);

        //mTextView.setTextSize(getResources().getDimension(R.dimen.font_large));

        this.addView(mTextView, layoutParams);

        super.mImgRemove.bringToFront();
        super.mImgRemove.bringToFront();
    }

}
