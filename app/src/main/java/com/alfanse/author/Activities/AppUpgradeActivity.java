/*
 * Copyright (c) 2018. Alfanse Developers
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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_FORCE_UPGRADE;

public class AppUpgradeActivity extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.layout_loading)
    ViewGroup layoutLoading;
    @BindView(R.id.layout_content)
    ViewGroup layoutcontent;

    @BindView(R.id.text_skip)
    TextView textSkip;

    private Context mContext;
    private Activity mActivity;
    private boolean isForcedUpgrade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_upgrade);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = AppUpgradeActivity.this;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.hasExtra(BUNDLE_KEY_FORCE_UPGRADE)) {
                isForcedUpgrade = intent.getBooleanExtra(BUNDLE_KEY_FORCE_UPGRADE, false);
            }
        }
        if (isForcedUpgrade) {
            textSkip.setVisibility(View.GONE);
        } else {
            textSkip.setVisibility(View.VISIBLE);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                layoutLoading.setVisibility(View.VISIBLE);
                layoutcontent.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                layoutLoading.setVisibility(View.GONE);
                layoutcontent.setVisibility(View.VISIBLE);
            }

        });

        webView.loadUrl(Constants.WEB_URL_UPGRADE_APP);
    }

    @OnClick(R.id.text_skip)
    public void onSkipClick() {
        Intent targetIntent;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            targetIntent = new Intent(mActivity, HomeActivity.class);
        } else {
            targetIntent = new Intent(mActivity, SignInActivity.class);
        }
        startActivity(targetIntent);
        finish();
    }

    @OnClick(R.id.button_update_app)
    public void onUpgradeClick() {
        Utils.getInstance(mContext).goToPlayStore();
    }
}

