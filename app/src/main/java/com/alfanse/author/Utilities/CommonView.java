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
import com.alfanse.author.Interfaces.onReportItemSubmitListener;
import com.alfanse.author.Models.CustomDialog;
import com.alfanse.author.R;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


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

    public static void showToast(Context context, CharSequence text, int duration, int type) {
        switch (type) {
            case ToastType.DEFAULT: {
                Toasty.normal(context, text, duration).show();
                break;
            }

            case ToastType.ERROR: {
                Toasty.error(context, text, duration, true).show();
                break;
            }

            case ToastType.SUCCESS: {
                Toasty.success(context, text, duration, true).show();
                break;
            }

            case ToastType.WARNING: {
                Toasty.warning(context, text, duration, true).show();
                break;
            }
            case ToastType.INFO: {
                Toasty.info(context, text, duration, true).show();
                break;
            }
            default: {
                Toasty.normal(context, text, duration).show();
                break;
            }
        }
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

    public void showReportDialog(ArrayList<String> listReport, Activity activity, final onReportItemSubmitListener listener) {

        final String[] selectedReportReasonTitle = new String[1];
        final String[] arrayReport = listReport.toArray(new String[listReport.size()]);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Set the alert dialog title
        builder.setTitle(mContext.getString(R.string.text_reason_for_report));

        // Add the choices
        builder.setSingleChoiceItems(arrayReport, -1, new DialogInterface.OnClickListener() {// Index of checked item (-1 = no selection)
            public void onClick(DialogInterface dialog, int which) {
                selectedReportReasonTitle[0] = arrayReport[which];
            }
        });
        // Add the buttons
        builder.setPositiveButton(R.string.action_report, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onReportItemSubmit(selectedReportReasonTitle[0]);
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class ToastType {
        public static final int DEFAULT = 0;
        public static final int ERROR = 1;
        public static final int SUCCESS = 2;
        public static final int WARNING = 3;
        public static final int INFO = 4;
    }
}
