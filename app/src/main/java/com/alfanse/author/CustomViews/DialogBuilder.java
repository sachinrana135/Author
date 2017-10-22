/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.CustomViews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.Utils;

/**
 * Created by Velocity-1601 on 5/12/2017.
 */

public class DialogBuilder extends AlertDialog.Builder {

    public static final int DEFAULT = 0;
    public static final int ERROR = 1;
    public static final int SUCCESS = 2;
    public static final int WARNING = 3;
    public static final int INFO = 4;

    private Context mContext;
    private View mView;
    private String mTitle;
    private int mDialogType;
    private ImageView mIconView;
    private TextView mTitleView;
    private View mHeaderLayout;
    private TextView mMessageView;
    private CharSequence mDialogMessage;
    private Boolean isHtml;



    public DialogBuilder(@NonNull Context context) {
        super(context);
        init(context);

    }

    public DialogBuilder(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {

        mContext = context;

        ContextThemeWrapper ctx = new ContextThemeWrapper(context, R.style.AppTheme);

        LayoutInflater layoutInflater = LayoutInflater.from(ctx);

        mView = layoutInflater.inflate(R.layout.layout_dialog, null);

        mHeaderLayout = mView.findViewById(R.id.header_layout_dialog);
        mIconView = (ImageView) mView.findViewById(R.id.icon_layout_dialog);
        mTitleView = (TextView) mView.findViewById(R.id.title_layout_dialog);
        mMessageView = (TextView) mView.findViewById(R.id.message_layout_dialog);
        setView(mView);
    }


    public void setDialogType(int dialogType) {

        switch (dialogType) {
            case DEFAULT: {
                mIconView.setImageDrawable(Utils.getInstance(mContext).getDrawable(R.drawable.ic_info_white_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                break;
            }

            case ERROR: {
                mIconView.setImageDrawable(Utils.getInstance(mContext).getDrawable(R.drawable.ic_sad_face_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorError));
                break;
            }

            case SUCCESS: {
                mIconView.setImageDrawable(Utils.getInstance(mContext).getDrawable(R.drawable.ic_check_circle_white_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorSuccess));
                break;
            }

            case WARNING: {
                mIconView.setImageDrawable(Utils.getInstance(mContext).getDrawable(R.drawable.ic_warning_white_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWarning));
                break;
            }

            case INFO: {
                mIconView.setImageDrawable(Utils.getInstance(mContext).getDrawable(R.drawable.ic_info_white_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorInfo));
                break;
            }
            default: {
                mIconView.setImageDrawable(Utils.getInstance(mContext).getDrawable(R.drawable.ic_info_white_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }
        }

    }

    @Override
    public DialogBuilder setMessage(@StringRes int messageId) {
        mMessageView.setText(mContext.getText(messageId));
        return this;
    }

    @Override
    public AlertDialog.Builder setMessage(@Nullable CharSequence message) {
        if (isHtml()) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                mMessageView.setText(Html.fromHtml(message.toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                mMessageView.setText(Html.fromHtml(message.toString()));
            }
        } else {
            mMessageView.setText(message);
        }
        return this;
    }

    @Override
    public AlertDialog.Builder setTitle(@StringRes int titleId) {
        mTitleView.setVisibility(View.VISIBLE);
        mTitleView.setText(mContext.getText(titleId));
        return this;
    }

    @Override
    public AlertDialog.Builder setTitle(@Nullable CharSequence title) {
        mTitleView.setVisibility(View.VISIBLE);
        mTitleView.setText(title);
        return this;
    }

    @Override
    public AlertDialog.Builder setIcon(@Nullable Drawable icon) {
        mIconView.setImageDrawable(icon);
        return this;
    }

    public void setHeaderColor(int colorId) {
        mHeaderLayout.setBackgroundColor(colorId);
    }

    public Boolean isHtml() {
        return isHtml;
    }

    public void setHtml(Boolean html) {
        isHtml = html;
    }
}
