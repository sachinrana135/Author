package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alfanse.author.Models.CustomDialog;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.CustomViews.DialogBuilder.ERROR;
import static com.alfanse.author.Utilities.Constants.MINIMUM_PASSWORD_LENGTH;

public class UpdatePasswordActivity extends BaseActivity {

    @BindView(R.id.edit_text_user_password_update_password)
    EditText editTextPassword;
    @BindView(R.id.edit_text_user_confirm_password_update_password)
    EditText editTextConfirmPassword;
    @BindView(R.id.button_update_password_update_password)
    Button buttonUpdatePassword;


    private Context mContext;
    private Activity mActivity;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mActivity = UpdatePasswordActivity.this;
        mAuth = FirebaseAuth.getInstance();
        initListener();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initListener() {

        buttonUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUpdatePasswordForm()) {
                    Utils.getInstance(mContext).hideSoftKeyboard(mActivity);
                    CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_update_password), null);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    String newPassword = editTextPassword.getText().toString();

                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    CommonView.getInstance(mContext).dismissProgressDialog();
                                    if (task.isSuccessful()) {
                                        CommonView.showToast(mActivity, getString(R.string.success_password_updated), Toast.LENGTH_LONG, CommonView.ToastType.SUCCESS);
                                        Intent userAccountIntent = new Intent(mActivity, UserAccountActivity.class);
                                        startActivity(userAccountIntent);
                                        finish();

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
        });

    }

    private boolean validateUpdatePasswordForm() {

        editTextPassword.setError(null);
        editTextConfirmPassword.setError(null);


        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

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
