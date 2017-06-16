package com.alfanse.author.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alfanse.author.CustomViews.DialogBuilder;
import com.alfanse.author.Models.CustomDialog;
import com.alfanse.author.R;

/**
 * Created by Velocity-1601 on 5/9/2017.
 */

public class CommonView {

    private static CommonView sInstance;
    private static Context mContext;
    private ProgressDialog mProgressDialog;

    private CommonView(Context context) {
        mContext = context;
    }

    public static synchronized CommonView getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CommonView(context);
        }
        return sInstance;
    }

    public void showTransparentProgressDialog(Activity activity, String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }

        ContextThemeWrapper ctx = new ContextThemeWrapper(mContext, R.style.AppTheme);

        LayoutInflater layoutInflater = LayoutInflater.from(ctx);

        View view = layoutInflater.inflate(R.layout.item_progress_bar, null);

        TextView messageView = (TextView) view.findViewById(R.id.message_item_progress);

        messageView.setText(message);

        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        mProgressDialog.setContentView(view);

    }

    public void showProgressDialog(Activity activity, String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void showProgressDialog(Activity activity, String message, String title) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public void showDialog(CustomDialog customDialog) {

        DialogBuilder builder = new DialogBuilder(customDialog.getActivity());
        // Add the buttons
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // Set other dialog properties
        if (customDialog.getTitle() != null) {
            builder.setTitle(customDialog.getTitle());
        }
        if (customDialog.getMessage() != null) {
            builder.setMessage(customDialog.getMessage());
        }
        builder.setDialogType(customDialog.getDialogType());

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
