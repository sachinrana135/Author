/*
 * Copyright (c) 2017. Alfanse Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.alfanse.author.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import net.alhazmy13.imagefilter.ImageFilter;



/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class QuoteCanvas extends SquareFrameLayout {

    private Context mContext;
    private ImageView mImageView;
    private ComponentTextView mDefaultComponentTextView = null;
    private ProgressBar progressBar;
    private Bitmap mOriginalBitmap = null;
    private ImageFilter.Filter mFilter = null;
    private Handler mHandler;

    public QuoteCanvas(Context context) {
        super(context);
        mContext = context;
        mImageView = new ImageView(mContext);
        mHandler = new Handler(Looper.getMainLooper());
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

        progressBar = new ProgressBar(mContext);
        FrameLayout.LayoutParams progressLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        progressLayoutParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(progressLayoutParams);
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
        mOriginalBitmap = bitmap;
        mImageView.setImageBitmap(bitmap);
        updateFilter();
    }

    private void updateFilter() {
        if (getFilter() != null) {
            setFilter(getFilter());
        }
    }


    public void setBackground(int color) {
        mImageView.setImageDrawable(null);
        mImageView.setBackgroundColor(color);
    }

    public void setBackground(Uri croppedImageUri) {
        mImageView.setImageURI(croppedImageUri);
        mOriginalBitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        updateFilter();
    }

    public void setBackground(String imageUrl) {

        addProgressBar();

        RequestOptions canvasImageOptions = new RequestOptions()
                .fitCenter()
                .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                .centerCrop();

        Glide.with(mContext)
                .asBitmap()
                .load(imageUrl)
                .apply(canvasImageOptions)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        removeProgressBar();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        removeProgressBar();
                        return false;
                    }
                })
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mOriginalBitmap = resource;
                        mImageView.setImageBitmap(resource);
                        updateFilter();
                    }
                });
    }

    public void addProgressBar() {
        this.addView(progressBar);
    }

    public void removeProgressBar() {
        if (this.indexOfChild(progressBar) != -1) {
            this.removeView(progressBar);
        }
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

    public void setFilter(final ImageFilter.Filter filter) {

        mFilter = filter;

        final Handler handler = new Handler(Looper.getMainLooper());

        addProgressBar();

        new Thread(new Runnable() {
            @Override
            public void run() {

                final Bitmap b = ImageFilter.applyFilter(mOriginalBitmap, filter);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (filter != null) {
                            mImageView.setImageBitmap(b);
                        } else {
                            mImageView.setImageBitmap(mOriginalBitmap);
                        }
                        removeProgressBar();
                    }
                });
            }
        }).start();

    }

    public ImageFilter.Filter getFilter() {
        return mFilter;
    }
}
