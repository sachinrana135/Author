package com.alfanse.author.CustomViews;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Velocity-1601 on 4/18/2017.
 */

public class ComponentImageView extends ComponentView {

    private Context mContext;
    private QuoteCanvas mCanvas;
    private ImageView mImageView;
    private Uri mUri;

    public ComponentImageView(@NonNull Context context, @NonNull QuoteCanvas canvas, Uri imageUri) {
        super(context, canvas);
        super.setScalable(true);
        mContext = context;
        mCanvas = canvas;
        mImageView = new ImageView(mContext);
        mUri = imageUri;
        Draw();
    }

    private void Draw() {

        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageView.setImageURI(mUri);
        mImageView.setAdjustViewBounds(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        addView(mImageView);

        super.mImgRemove.bringToFront();
        super.mImgResize.bringToFront();
    }
}
