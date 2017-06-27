package com.alfanse.author.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

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

        RequestOptions canvasImageOptions = new RequestOptions()
                .fitCenter()
                .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                .centerCrop();

        Glide.with(mContext)
                .load(imageUrl)
                .apply(canvasImageOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        QuoteCanvas.this.removeView(progressBar);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        QuoteCanvas.this.removeView(progressBar);
                        return false;
                    }
                })
                .into(mImageView);
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
