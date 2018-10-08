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

import android.content.Context;
import android.os.Bundle;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApiMaintenanceActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_maintenance);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
    }

    @OnClick(R.id.button_exit)
    void onExitButtonClick() {
        Utils.getInstance(mContext).closeApplication();
    }

}
