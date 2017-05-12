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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonMethod;

/**
 * Created by Velocity-1601 on 5/12/2017.
 */

public class DialogBuilder extends AlertDialog.Builder {

    public static final int DEFAULT = 0;
    public static final int ERROR = 1;
    public static final int SUCCESS = 2;
    public static final int WARNING = 3;

    private Context mContext;
    private View mView;
    private String mTitle;
    private int mDialogType;
    private ImageView mIconView;
    private View mHeaderLayout;
    private TextView mMessageView;
    private CharSequence mDialogMessage;


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
        mMessageView = (TextView) mView.findViewById(R.id.message_layout_dialog);
        setView(mView);
    }


    public void setDialogType(int dialogType) {

        switch (dialogType) {
            case DEFAULT: {
                mIconView.setImageDrawable(CommonMethod.getInstance(mContext).getDrawable(R.drawable.ic_info_white_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                break;
            }

            case ERROR: {
                mIconView.setImageDrawable(CommonMethod.getInstance(mContext).getDrawable(R.drawable.ic_error_white_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorError));
                break;
            }

            case SUCCESS: {
                mIconView.setImageDrawable(CommonMethod.getInstance(mContext).getDrawable(R.drawable.ic_check_circle_white_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorSuccess));
                break;
            }

            case WARNING: {
                mIconView.setImageDrawable(CommonMethod.getInstance(mContext).getDrawable(R.drawable.ic_warning_white_24dp));
                mHeaderLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWarning));
                break;
            }
            default: {
                mIconView.setImageDrawable(CommonMethod.getInstance(mContext).getDrawable(R.drawable.ic_info_white_24dp));
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
    public AlertDialog.Builder setIcon(@Nullable Drawable icon) {
        mIconView.setImageDrawable(icon);
        return this;
    }

    public void setHeaderColor(int colorId) {
        mHeaderLayout.setBackgroundColor(colorId);
    }
}
