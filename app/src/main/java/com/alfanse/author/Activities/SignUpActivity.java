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

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanse.author.CustomViews.DialogBuilder;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.CustomDialog;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.CustomViews.DialogBuilder.ERROR;
import static com.alfanse.author.Utilities.Constants.MINIMUM_PASSWORD_LENGTH;

public class SignUpActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION_GET_ACCOUNTS = 3750;
    @BindView(R.id.edit_text_user_name_sign_up)
    EditText editTextName;
    @BindView(R.id.edit_text_user_email_sign_up)
    AutoCompleteTextView editTextEmail;
    @BindView(R.id.edit_text_user_password_sign_up)
    EditText editTextPassword;
    @BindView(R.id.edit_text_user_confirm_password_sign_up)
    EditText editTextConfirmPassword;
    @BindView(R.id.button_sign_up_sign_up)
    Button buttonSignUp;
    @BindView(R.id.text_sign_in_sign_up)
    TextView textSignIn;


    private Context mContext;
    private Activity mActivity;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mActivity = SignUpActivity.this;
        mAuth = FirebaseAuth.getInstance();
        initListener();
        // checkGetAccountsPermission();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void checkGetAccountsPermission() {

        String[] PERMISSIONS = {Manifest.permission.GET_ACCOUNTS};

        if (!Utils.getInstance(mContext).hasPermissions(PERMISSIONS)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, REQUEST_CODE_PERMISSION_GET_ACCOUNTS);
            }
        } else {
            addEmailAdapter();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_GET_ACCOUNTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addEmailAdapter();
                } else {
                    CommonView.showToast(mActivity, getString(R.string.warning_permission_denied), Toast.LENGTH_LONG, CommonView.ToastType.WARNING);
                }
                break;
            }
        }
    }

    private void addEmailAdapter() {

        Account[] accounts = AccountManager.get(this).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        for (Account account : accounts) {
            if ((account.name).matches(Constants.PATTERN_EMAIL)) {
                emailSet.add(account.name);
            }
        }
        editTextEmail.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));
    }

    private void initListener() {

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSignUpForm()) {
                    Utils.getInstance(mContext).hideSoftKeyboard(mActivity);
                    CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_user_signup), getString(R.string.text_please_wait));
                    mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString())
                            .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    CommonView.getInstance(mContext).dismissProgressDialog();
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if (currentUser != null) {
                                            updateProfileName();
                                        }

                                    } else {
                                        CommonView.getInstance(mContext).dismissProgressDialog();
                                        CommonView.getInstance(mContext).showDialog(
                                                new CustomDialog().setActivity(mActivity)
                                                        .setDialogType(ERROR)
                                                        .setTitle(getString(R.string.error_exception))
                                                        .setMessage(task.getException().getMessage())
                                        );
                                    }
                                }
                            });
                }
            }
        });

        textSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(mActivity, SignInActivity.class);
                startActivity(signInIntent);
            }
        });

        /*editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextEmail.showDropDown();

            }
        });*/
    }

    private void updateProfileName() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(editTextName.getText().toString().trim())
                .build();
        mAuth.getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            addUser();
                        } else {
                            CommonView.getInstance(mContext).dismissProgressDialog();
                            CommonView.getInstance(mContext).showDialog(
                                    new CustomDialog().setActivity(mActivity)
                                            .setDialogType(ERROR)
                                            .setTitle(getString(R.string.error_exception))
                                            .setMessage(task.getException().getMessage())
                            );
                        }
                    }
                });
    }

    private void sendVerificationEmail() {

        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_verification_email), getString(R.string.text_please_wait));
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.signOut();
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        if (task.isSuccessful()) {
                            showAccountCreatedSuccessDialog();
                            CommonView.getInstance(mContext).dismissProgressDialog();
                        } else {
                            // If sign in fails, display a message to the user.
                            CommonView.getInstance(mContext).dismissProgressDialog();
                            CommonView.getInstance(mContext).showDialog(
                                    new CustomDialog().setActivity(mActivity)
                                            .setDialogType(ERROR)
                                            .setTitle(getString(R.string.error_exception))
                                            .setMessage(task.getException().getMessage())
                            );
                        }
                    }
                });

    }

    private void addUser() {

        Author author = new Author();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        author.setFirebaseId(currentUser.getUid());
        author.setName(currentUser.getDisplayName());
        author.setEmail(currentUser.getEmail());
        addUserOnWeb(author);
    }

    private void addUserOnWeb(Author author) {

        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR, new Gson().toJson(author));
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_SAVE_AUTHOR)
                .setParams(param)
                .setMessage("SignUpActivity.java|addUserOnWeb")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            CommonView.getInstance(mContext).dismissProgressDialog();
                            sendVerificationEmail();
                        } catch (Exception e) {
                            CommonView.getInstance(mContext).dismissProgressDialog();
                            Utils.getInstance(mContext).logException(e);
                        }
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void showAccountCreatedSuccessDialog() {

        DialogBuilder builder = new DialogBuilder(mActivity);
        // Add the buttons
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mAuth.signOut();
                Intent signInIntent = new Intent(mActivity, SignInActivity.class);
                startActivity(signInIntent);
            }
        });
        // Set other dialog properties
        builder.setTitle(getString(R.string.success_signup));
        builder.setMessage(R.string.msg_signup_success);
        builder.setDialogType(DialogBuilder.SUCCESS);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private boolean validateSignUpForm() {
        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        editTextConfirmPassword.setError(null);

        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.error_required));
            editTextName.requestFocus();
            return false;
        }

        if (!email.matches(Constants.PATTERN_EMAIL)) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            editTextEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.error_required));
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            editTextPassword.setError(getString(R.string.error_password_length));
            editTextPassword.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError(getString(R.string.error_required));
            editTextConfirmPassword.requestFocus();
            return false;
        }

        if (!confirmPassword.equals(password)) {
            editTextConfirmPassword.setError(getString(R.string.error_password_mismatch));
            editTextConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(menuItem);

        }
    }
}
