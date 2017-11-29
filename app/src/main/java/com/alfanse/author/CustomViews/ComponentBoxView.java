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
