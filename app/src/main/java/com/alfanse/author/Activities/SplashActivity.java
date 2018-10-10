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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.NetworkUtils;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.StartUpConfig;
import com.alfanse.author.Utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_FORCE_UPGRADE;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Context mContext;
    private Activity mActivity;
    private View mContentView;
    private StartUpConfig instance;
    private boolean mIsNetworkConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mContentView = findViewById(R.id.fullscreen_content_splash);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

    }

    private void getStartUpConfig() {
        progressBar.setVisibility(View.VISIBLE);
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_STARTUP_CONFIG)
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            progressBar.setVisibility(View.GONE);

                            instance = StartUpConfig.getInstance();
                            instance = new Gson().fromJson(stringResponse, StartUpConfig.class);

                            //check if api is on maintenance
                            if (!instance.getApiStatus()) {
                                Intent intent = new Intent(mActivity, ApiMaintenanceActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }

                            //Check if update is available
                            Boolean shouldShowUpgrade = Utils.getInstance(mContext).shouldShowUpgrade();
                            SharedManagement.getInstance(mContext).setInt(SharedManagement.TOTAL_APP_LAUNCHED, SharedManagement.getInstance(mContext).getInt(SharedManagement.TOTAL_APP_LAUNCHED) + 1);
                            if (instance.isUpdateAvailable() && shouldShowUpgrade) {
                                Intent intent = new Intent(mActivity, AppUpgradeActivity.class);
                                intent.putExtra(BUNDLE_KEY_FORCE_UPGRADE, instance.isForceUpdate());
                                startActivity(intent);
                                finish();
                                return;
                            }

                            Intent targetIntent;
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                targetIntent = new Intent(mActivity, HomeActivity.class);
                            } else {
                                targetIntent = new Intent(mActivity, SignInActivity.class);
                            }
                            startActivity(targetIntent);
                            finish();

                        } catch (Exception e) {
                            Utils.getInstance(mContext).logException(e);
                        }
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
        api.call();
        //endregion API_CALL_END
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = getApplicationContext();
        mActivity = SplashActivity.this;
        checkInternetConnectivity();
    }

    private void checkInternetConnectivity() {
        mIsNetworkConnected = NetworkUtils.getInstance(mContext).isNetworkConnected();
        if (!mIsNetworkConnected) {
            Intent intent = new Intent(mActivity, NoInternetConnectionActivity.class);
            startActivity(intent);
        } else {
            getStartUpConfig();
        }
    }
}
