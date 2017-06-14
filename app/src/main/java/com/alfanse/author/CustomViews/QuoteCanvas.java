package com.alfanse.author.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class QuoteCanvas extends SquareFrameLayout {

    private Context mContext;
    private ImageView mImageView;
    private ComponentTextView mDefaultComponentTextView = null;

    public QuoteCanvas(Context context) {
        super(context);
        mContext = context;
        mImageView = new ImageView(mContext);
    }

    public QuoteCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageView.setAdjustViewBounds(true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        addView(mImageView);
    }

    public QuoteCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageView.setAdjustViewBounds(true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        addView(mImageView);
    }

    public void setBackground(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    public void setBackground(int color) {
        mImageView.setImageDrawable(null);
        mImageView.setBackgroundColor(color);
    }

    public void setBackground(Uri croppedImageUri) {
        mImageView.setImageURI(croppedImageUri);
    }

    public void setBackground(String imageUrl) {

        final ProgressBar progressBar = new ProgressBar(mContext);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(layoutParams);
        this.addView(progressBar);

        Picasso.with(mContext)
                .load(imageUrl)
                .fit()
                .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        QuoteCanvas.this.removeView(progressBar);
                    }

                    @Override
                    public void onError() {
                        QuoteCanvas.this.removeView(progressBar);
                    }
                });
    }

    public void setStateFocused() {
        UnFocusChildren();
    }

    private void UnFocusChildren() {

        for (int index = 0; index < this.getChildCount(); ++index) {
            View componentView = this.getChildAt(index);

            if ((componentView instanceof ComponentView)) {

                ((ComponentView) componentView).setStateFocused(false);

            }
        }
    }

}
