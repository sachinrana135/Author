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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alfanse.author.Models.Filter;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.GPUImageFilterTools;
import com.alfanse.author.Utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;

import static com.alfanse.author.Utilities.GPUImageFilterTools.FilterType.BRIGHTNESS;
import static com.alfanse.author.Utilities.GPUImageFilterTools.FilterType.CONTRAST;
import static com.alfanse.author.Utilities.GPUImageFilterTools.FilterType.HUE;
import static com.alfanse.author.Utilities.GPUImageFilterTools.FilterType.SATURATION;
import static com.alfanse.author.Utilities.GPUImageFilterTools.FilterType.TRANSFORM2D;
import static com.alfanse.author.Utilities.GPUImageFilterTools.FilterType.VIGNETTE;


/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class QuoteCanvas extends SquareFrameLayout {

    private GLSurfaceView mGLSurfaceView;
    private Context mContext;
    private GPUImage mImageView;
    private ImageView mTempImageView;
    private ProgressBar progressBar;
    private Filter mFilter = null;
    public Size mForceSize = null;
    private float mRatio = 0.0f;
    private Bitmap mOriginalBitmap = null;
    GPUImageFilterGroup mGpuImageGroupFilter = new GPUImageFilterGroup();
    private LinkedList<Filter> mFilters = new LinkedList<>();
    private LinkedList<GPUImageFilterTools.FilterType> allowMergeFilters = new LinkedList<>(Arrays.asList(BRIGHTNESS, CONTRAST, HUE, SATURATION, VIGNETTE, TRANSFORM2D));

    public QuoteCanvas(Context context) {
        super(context);
        init(context, null);
    }

    public QuoteCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mGLSurfaceView = new GPUImageGLSurfaceView(context, attrs);
        addView(mGLSurfaceView);

        progressBar = new ProgressBar(mContext, null,
                android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(true);
        FrameLayout.LayoutParams progressLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        progressLayoutParams.gravity = Gravity.BOTTOM;

        mImageView = new GPUImage(getContext());
        mImageView.setGLSurfaceView(mGLSurfaceView);

        mTempImageView = new ImageView(mContext);
        mTempImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mTempImageView.setAdjustViewBounds(true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mTempImageView.setLayoutParams(layoutParams);
        mTempImageView.setVisibility(GONE);
        addView(mTempImageView);
    }

    public GPUImageFilter getFilterByType(Filter filter) {
        int key = getFilterKeyFromGroup(filter);
        return key != -1 ? mGpuImageGroupFilter.getFilters().get(key) : null;
    }

    private int getFilterKeyFromGroup(Filter filter) {
        int i = -1;
        for (Filter n : mFilters) {
            i++;
            if (n.getFilter() == filter.getFilter()) {
                break;
            }
        }
        return i;
    }

    public boolean isFilterApplied(Filter filter) {
        for (Filter n : mFilters) {
            if (n.getFilter() == filter.getFilter()) {
                return true;
            }
        }
        return false;
    }

    public GPUImageFilter getFilter() {
        return mGpuImageGroupFilter;
    }

    private class GPUImageGLSurfaceView extends GLSurfaceView {
        public GPUImageGLSurfaceView(Context context) {
            super(context);
        }

        public GPUImageGLSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (mForceSize != null) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mForceSize.width, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(mForceSize.height, MeasureSpec.EXACTLY));
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    public static class Size {
        int width;
        int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public void setBackground(Bitmap bitmap) {
        mImageView.deleteImage();
        mImageView.setFilter(new GPUImageFilter());
        mImageView.setImage(bitmap);
        mOriginalBitmap = bitmap;
    }

    public void showTempImageView(Bitmap b) {
        mTempImageView.setImageBitmap(b);
        mTempImageView.setVisibility(VISIBLE);
    }

    public void hideTempImageView() {
        mTempImageView.setImageBitmap(null);
        mTempImageView.setVisibility(GONE);
    }


    public void setBackground(int color) {
        Bitmap b = createImageFromColor(getWidth(), getHeight(), color);
        setBackground(b);
    }

    /**
     * A one color image.
     *
     * @param width
     * @param height
     * @param color
     * @return A one color image with the given width and height.
     */
    public static Bitmap createImageFromColor(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }

    public void setBackground(Uri croppedImageUri) {
        mImageView.deleteImage();
        mImageView.setImage(croppedImageUri);
        mOriginalBitmap = mImageView.getBitmapWithFilterApplied();
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
                        setBackground(resource);
                    }
                });
    }

    public void addProgressBar() {
        if (this.indexOfChild(progressBar) == -1) {
            this.addView(progressBar);
        }
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

    public void setFilter(Filter filter) {
        mFilter = filter;
        if (mFilter.getFilter() != null) {
            createGroupFiltersList(filter);
            mImageView.setFilter(mGpuImageGroupFilter);
            requestRender();
        } else {
            //clear group filters
            mFilters.clear();
            setBackground(mOriginalBitmap);
        }
    }

    private void createGroupFiltersList(Filter filter) {

        if (mFilters != null) {
            //Check if filter is already exist
            Iterator<Filter> iterator = mFilters.iterator();
            while (iterator.hasNext()) {
                Filter f = iterator.next();
                if (f.getFilter().equals(filter.getFilter())) {
                    return;
                }
            }
            // check if filter can be grouped
            if (allowMergeFilters.contains(filter.getFilter())) {
                mFilters.add(filter);
                mGpuImageGroupFilter.getFilters().add(GPUImageFilterTools.createFilterForType(mContext, filter.getFilter()));
            } else {
                Iterator<Filter> iter = mFilters.iterator();
                int i = -0;
                while (iter.hasNext()) {
                    Filter g = iter.next();
                    if (!allowMergeFilters.contains(g.getFilter())) {
                        // delete filter
                        iter.remove();
                        mGpuImageGroupFilter.getFilters().remove(i);
                    }
                    i++;
                }
                mFilters.add(filter);
                mGpuImageGroupFilter.getFilters().add(GPUImageFilterTools.createFilterForType(mContext, filter.getFilter()));
            }
            mGpuImageGroupFilter = new GPUImageFilterGroup(mGpuImageGroupFilter.getFilters());
        }
    }

    public void requestRender() {
        mGLSurfaceView.requestRender();
    }

    public Bitmap getBitmapWithFilterApplied() {
        return mImageView.getBitmapWithFilterApplied();
    }
}
