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
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
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

import com.alfanse.author.Interfaces.bitmapFilterListener;
import com.alfanse.author.Models.Filter;
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
    private Filter mFilter = null;
    private Handler mHandler;
    private int hueLevel;
    private int brightnessLevel;
    private int contrastLevel;
    private int saturationLevel = 100;//default saturation

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
        mOriginalBitmap = null;
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

    public void setFilter(final Filter filter) {

        mFilter = filter;

        if (mOriginalBitmap != null) {
            if (mFilter.getFilter() != null) {

                final Handler handler = new Handler(Looper.getMainLooper());

                addProgressBar();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        final Bitmap b = ImageFilter.applyFilter(mOriginalBitmap, mFilter.getFilter());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                mImageView.setImageBitmap(b);

                                removeProgressBar();
                            }
                        });
                    }
                }).start();
            } else {
                mImageView.setImageBitmap(mOriginalBitmap);
            }
        }

    }

    public void applyBrightnessFilter(final int level, final bitmapFilterListener bitmapFilterListener) {

        brightnessLevel = level;

        if (mOriginalBitmap != null) {

            final Handler handler = new Handler(Looper.getMainLooper());

            addProgressBar();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Bitmap bmOut = null;

                    try {
                        // image size
                        int width = mOriginalBitmap.getWidth();
                        int height = mOriginalBitmap.getHeight();
                        // create output bitmap
                        bmOut = Bitmap.createBitmap(width, height, mOriginalBitmap.getConfig());
                        // color information
                        int A, R, G, B;
                        int pixel;

                        // scan through all pixels
                        for (int x = 0; x < width; ++x) {
                            for (int y = 0; y < height; ++y) {
                                // get pixel color
                                pixel = mOriginalBitmap.getPixel(x, y);
                                A = Color.alpha(pixel);
                                R = Color.red(pixel);
                                G = Color.green(pixel);
                                B = Color.blue(pixel);

                                // increase/decrease each channel
                                R += level;
                                if (R > 255) {
                                    R = 255;
                                } else if (R < 0) {
                                    R = 0;
                                }

                                G += level;
                                if (G > 255) {
                                    G = 255;
                                } else if (G < 0) {
                                    G = 0;
                                }

                                B += level;
                                if (B > 255) {
                                    B = 255;
                                } else if (B < 0) {
                                    B = 0;
                                }

                                // apply new pixel color to output bitmap
                                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
                            }
                        }
                    } catch (Exception e) {
                        bitmapFilterListener.onError();
                    }

                    final Bitmap finalBmOut = bmOut;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalBmOut != null) {
                                mImageView.setImageBitmap(finalBmOut);
                                bitmapFilterListener.onSuccuess();
                            } else {
                                bitmapFilterListener.onError();
                            }
                            removeProgressBar();
                        }
                    });
                }
            }).start();

        }
    }

    public void applyContrastFilter(final int level, final bitmapFilterListener bitmapFilterListener) {

        contrastLevel = level;

        final double castedLevel = level;

        if (mOriginalBitmap != null) {

            final Handler handler = new Handler(Looper.getMainLooper());

            addProgressBar();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Bitmap bmOut = null;

                    try {
                        // image size
                        int width = mOriginalBitmap.getWidth();
                        int height = mOriginalBitmap.getHeight();
                        // create output bitmap

                        // create a mutable empty bitmap
                        bmOut = Bitmap.createBitmap(width, height, mOriginalBitmap.getConfig());

                        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
                        Canvas c = new Canvas();
                        c.setBitmap(bmOut);

                        // draw bitmap to bmOut from src bitmap so we can modify it
                        c.drawBitmap(mOriginalBitmap, 0, 0, new Paint(Color.BLACK));


                        // color information
                        int A, R, G, B;
                        int pixel;
                        // get contrast value
                        double contrast = Math.pow((100 + castedLevel) / 100, 2);

                        // scan through all pixels
                        for (int x = 0; x < width; ++x) {
                            for (int y = 0; y < height; ++y) {
                                // get pixel color
                                pixel = mOriginalBitmap.getPixel(x, y);
                                A = Color.alpha(pixel);
                                // apply filter contrast for every channel R, G, B
                                R = Color.red(pixel);
                                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                                if (R < 0) {
                                    R = 0;
                                } else if (R > 255) {
                                    R = 255;
                                }

                                G = Color.green(pixel);
                                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                                if (G < 0) {
                                    G = 0;
                                } else if (G > 255) {
                                    G = 255;
                                }

                                B = Color.blue(pixel);
                                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                                if (B < 0) {
                                    B = 0;
                                } else if (B > 255) {
                                    B = 255;
                                }

                                // set new pixel color to output bitmap
                                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
                            }
                        }

                    } catch (Exception e) {
                        bitmapFilterListener.onError();
                    }

                    final Bitmap finalBmOut = bmOut;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalBmOut != null) {
                                mImageView.setImageBitmap(finalBmOut);
                                bitmapFilterListener.onSuccuess();
                            } else {
                                bitmapFilterListener.onError();
                            }
                            removeProgressBar();
                        }
                    });
                }
            }).start();

        }
    }

    public void applySaturationFilter(final int level, final bitmapFilterListener bitmapFilterListener) {

        saturationLevel = level;

        if (mOriginalBitmap != null) {

            final Handler handler = new Handler(Looper.getMainLooper());

            addProgressBar();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Bitmap bmOut = null;

                    try {
                        float f_value = (float) (level / 100.0);

                        int w = mOriginalBitmap.getWidth();
                        int h = mOriginalBitmap.getHeight();

                        bmOut = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        Canvas canvasResult = new Canvas(bmOut);
                        Paint paint = new Paint();
                        ColorMatrix colorMatrix = new ColorMatrix();
                        colorMatrix.setSaturation(f_value);
                        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
                        paint.setColorFilter(filter);
                        canvasResult.drawBitmap(mOriginalBitmap, 0, 0, paint);

                    } catch (Exception e) {
                        bitmapFilterListener.onError();
                    }

                    final Bitmap finalBmOut = bmOut;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalBmOut != null) {
                                mImageView.setImageBitmap(finalBmOut);
                                bitmapFilterListener.onSuccuess();
                            } else {
                                bitmapFilterListener.onError();
                            }
                            removeProgressBar();
                        }
                    });
                }
            }).start();

        }
    }

    public void applyTint(final int color, final bitmapFilterListener bitmapFilterListener) {

        if (mOriginalBitmap != null) {

            final Handler handler = new Handler(Looper.getMainLooper());

            addProgressBar();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Bitmap bmOut = null;

                    try {
                        // image size
                        int width = mOriginalBitmap.getWidth();
                        int height = mOriginalBitmap.getHeight();
                        // create output bitmap

                        // create a mutable empty bitmap
                        bmOut = Bitmap.createBitmap(width, height, mOriginalBitmap.getConfig());

                        Paint p = new Paint(Color.RED);
                        ColorFilter filter = new LightingColorFilter(color, 1);
                        p.setColorFilter(filter);

                        Canvas c = new Canvas();
                        c.setBitmap(bmOut);
                        c.drawBitmap(mOriginalBitmap, 0, 0, p);

                    } catch (Exception e) {
                        bitmapFilterListener.onError();
                    }

                    final Bitmap finalBmOut = bmOut;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalBmOut != null) {
                                mImageView.setImageBitmap(finalBmOut);
                                bitmapFilterListener.onSuccuess();
                            } else {
                                bitmapFilterListener.onError();
                            }
                            removeProgressBar();
                        }
                    });
                }
            }).start();

        }
    }

    public void applyHueFilter(final int level, final bitmapFilterListener bitmapFilterListener) {

        hueLevel = level;

        final float castedHueLevel = level;

        if (mOriginalBitmap != null) {

            final Handler handler = new Handler(Looper.getMainLooper());

            addProgressBar();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Bitmap bmOut = null;

                    try {
                        bmOut = mOriginalBitmap.copy(mOriginalBitmap.getConfig(), true);
                        int width = bmOut.getWidth();
                        int height = bmOut.getHeight();

                        float[] hsv = new float[3];

                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                int pixel = bmOut.getPixel(x, y);
                                Color.colorToHSV(pixel, hsv);
                                hsv[0] = castedHueLevel;
                                bmOut.setPixel(x, y, Color.HSVToColor(Color.alpha(pixel), hsv));
                            }
                        }

                    } catch (Exception e) {
                        bitmapFilterListener.onError();
                    }

                    final Bitmap finalBmOut = bmOut;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalBmOut != null) {
                                mImageView.setImageBitmap(finalBmOut);
                                bitmapFilterListener.onSuccuess();
                            } else {
                                bitmapFilterListener.onError();
                            }
                            removeProgressBar();
                        }
                    });
                }
            }).start();

        }
    }


    public Filter getFilter() {
        return mFilter;
    }

    public int getHueLevel() {
        return hueLevel;
    }

    public int getBrightnessLevel() {
        return brightnessLevel;
    }

    public int getContrastLevel() {
        return contrastLevel;
    }

    public int getSaturationLevel() {
        return saturationLevel;
    }

    public void resetOriginalBitmap() {
        mImageView.setImageBitmap(mOriginalBitmap);
    }
}
