package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Context mContext;
    private Activity mActivity;
    private Boolean mIsNetworkConnected;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mContext = getApplicationContext();
        mActivity = BaseActivity.this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkInternetConnectivity();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in
            //Toast.makeText(mActivity, getString(R.string.error_user_session_expired), Toast.LENGTH_LONG).show();
        }
    }

    private void checkInternetConnectivity() {
        mIsNetworkConnected = NetworkUtils.getInstance(mContext).isNetworkConnected();
        if (!mIsNetworkConnected) {
            showNoInternetSnackBar();
        }
    }

    public void showNoInternetSnackBar() {
        mSnackbar = Snackbar
                .make(getWindow().getDecorView().getRootView(), getString(R.string.error_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle user action
                        mSnackbar.dismiss();
                        finish();
                        startActivity(getIntent());
                    }
                });
        mSnackbar.setActionTextColor(ContextCompat.getColor(mContext, R.color.colorSuccess));
        View snackbarView = mSnackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        mSnackbar.show();
    }
}
