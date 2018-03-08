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

package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_CAME_VIA_NOTIFICATION;

public class BaseActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Context mContext;
    private Activity mActivity;
    private Boolean mIsNetworkConnected;
    private Snackbar mSnackbar;
    private Boolean isCameViaNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mContext = getApplicationContext();
        mActivity = BaseActivity.this;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(BUNDLE_KEY_CAME_VIA_NOTIFICATION)) {
                isCameViaNotification = intent.getBooleanExtra(BUNDLE_KEY_CAME_VIA_NOTIFICATION, false);
            }
        }
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
            //showNoInternetSnackBar();
            CommonView.getInstance(mContext).showNoInternetDialog(mActivity);
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

    @Override
    public void onBackPressed() {
        if (isCameViaNotification) {
            Intent i = new Intent(mActivity, HomeActivity.class);
            startActivity(i);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        CommonView.getInstance(mContext).dismissProgressDialog();
        super.onDestroy();
    }
}
