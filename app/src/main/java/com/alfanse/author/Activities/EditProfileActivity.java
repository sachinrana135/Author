/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.CustomDialog;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.CustomViews.DialogBuilder.ERROR;

public class EditProfileActivity extends BaseActivity {

    @BindView(R.id.toolbar_edit_profile)
    Toolbar mToolbar;
    @BindView(R.id.edit_text_edit_name_edit_profile)
    EditText editTextName;
    @BindView(R.id.edit_text_edit_email_edit_profile)
    EditText editTextEmail;
    @BindView(R.id.edit_text_edit_mobile_edit_profile)
    EditText editTextMobile;
    @BindView(R.id.edit_text_edit_dob_edit_profile)
    EditText editTextDob;
    @BindView(R.id.edit_text_edit_status_edit_profile)
    EditText editTextStatus;
    @BindView(R.id.radio_group_gender_edit_profile)
    RadioGroup radioGroupGender;
    Boolean isWebProfileToBeUpdated = true;
    Boolean isFirebaseNameToBeUpdated = false;
    Boolean isFirebaseEmailToBeUpdated = false;
    private FirebaseAuth mAuth;
    private Activity mActivity;
    private Context mContext;
    private Author mAuthor;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = EditProfileActivity.this;
        mAuth = FirebaseAuth.getInstance();

        initToolbar();
        initListener();
        mAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
        renderView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initListener() {

        editTextDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
                                calendar.set(year, monthOfYear, dayOfMonth);
                                String formattedDate = dateFormatter.format(calendar.getTime());
                                editTextDob.setText(formattedDate);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(FontHelper.getCustomTypefaceTitle(getString(R.string.title_edit_profile)));
    }


    private void renderView() {

        editTextName.setText(mAuthor.getName());
        editTextStatus.setText(mAuthor.getStatus());
        editTextEmail.setText(mAuthor.getEmail());
        editTextMobile.setText(mAuthor.getMobile());
        editTextDob.setText(mAuthor.getDob());

        if (mAuthor.getGender() != null) {
            if (mAuthor.getGender().equalsIgnoreCase("Male")) {
                radioGroupGender.check(R.id.radio_male_edit_profile);
            } else if (mAuthor.getGender().equalsIgnoreCase("Female")) {
                radioGroupGender.check(R.id.radio_female_edit_profile);
            }
        }
        if (mAuthor.getEmail() != null) {
            if (!mAuthor.getEmail().equalsIgnoreCase("") && !mAuthor.getEmail().equalsIgnoreCase(null)) {
                editTextEmail.setKeyListener(null);
                editTextEmail.setEnabled(false);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_save_edit_profile:

                if (validateEditProfileForm()) {
                    Utils.getInstance(mContext).hideSoftKeyboard(mActivity);
                    updateProfile();
                }

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(menuItem);

        }
    }

    private boolean validateEditProfileForm() {

        editTextName.setError(null);
        editTextEmail.setError(null);

        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();

        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.error_required));
            editTextName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.error_required));
            editTextEmail.requestFocus();
            return false;
        }

        if (!email.matches(Constants.PATTERN_EMAIL)) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            editTextEmail.requestFocus();
            return false;
        }

        if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            CommonView.showToast(mContext, getString(R.string.error_no_gender_selected), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
            return false;
        }

        return true;

    }

    private void updateProfile() {

        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_update_profile), null);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mAuthor.setName(editTextName.getText().toString().trim());
        mAuthor.setEmail(editTextEmail.getText().toString().trim());
        mAuthor.setDob(editTextDob.getText().toString().trim());
        mAuthor.setMobile(editTextMobile.getText().toString().trim());
        mAuthor.setStatus(editTextStatus.getText().toString().trim());

        if (radioGroupGender.getCheckedRadioButtonId() != -1) {
            int id = radioGroupGender.getCheckedRadioButtonId();
            View radioButton = radioGroupGender.findViewById(id);
            int radioId = radioGroupGender.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) radioGroupGender.getChildAt(radioId);
            String selection = (String) btn.getText();
            mAuthor.setGender(selection);
        }

        if (!mAuthor.getName().equals(firebaseUser.getDisplayName())) {
            isFirebaseNameToBeUpdated = true;
            updateFirebaseUserName();
        }


        if (!mAuthor.getEmail().equals(firebaseUser.getEmail())) {
            isFirebaseEmailToBeUpdated = true;
            updateFirebaseUserEmail();
        }

        updateProfileOnWeb();
    }

    private void updateFirebaseUserEmail() {

        firebaseUser.updateEmail(editTextEmail.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        isFirebaseEmailToBeUpdated = false;
                        if (task.isSuccessful()) {

                            if (!isWebProfileToBeUpdated && !isFirebaseNameToBeUpdated && !isFirebaseEmailToBeUpdated) {
                                postProfileUpdateAction();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
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

    private void postProfileUpdateAction() {

        CommonView.getInstance(mContext).dismissProgressDialog();
        SharedManagement.getInstance(mContext).setLoggedUser(mAuthor);
        CommonView.showToast(mActivity, getString(R.string.success_profile_updated), Toast.LENGTH_LONG, CommonView.ToastType.SUCCESS);
        Intent userAccountIntent = new Intent(mActivity, UserAccountActivity.class);
        startActivity(userAccountIntent);
        finish();

    }

    private void updateProfileOnWeb() {

        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR, new Gson().toJson(mAuthor));
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_UPDATE_AUTHOR)
                .setParams(param)
                .setMessage("EditProfileActivity.java|updateProfileOnWeb")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        isWebProfileToBeUpdated = false;
                        if (!isWebProfileToBeUpdated && !isFirebaseNameToBeUpdated && !isFirebaseEmailToBeUpdated) {
                            postProfileUpdateAction();
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

    private void updateFirebaseUserName() {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(editTextName.getText().toString().trim())
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        CommonView.getInstance(mContext).dismissProgressDialog();
                        isFirebaseNameToBeUpdated = false;
                        if (task.isSuccessful()) {
                            if (!isWebProfileToBeUpdated && !isFirebaseNameToBeUpdated && !isFirebaseEmailToBeUpdated) {
                                postProfileUpdateAction();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
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
