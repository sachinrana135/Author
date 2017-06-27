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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanse.author.CustomViews.DialogBuilder;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.CustomDialog;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.alfanse.author.CustomViews.DialogBuilder.ERROR;
import static com.alfanse.author.CustomViews.DialogBuilder.SUCCESS;
import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_AUTHOR;

public class SignInActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 2545;
    private static final int REQUEST_CODE_PERMISSION_GET_ACCOUNTS = 4739;
    @BindView(R.id.button_sdk_facebook_login_sign_in)
    LoginButton buttonSdkFacebookLogin;
    @BindView(R.id.button_sdk_google_login_sign_in)
    SignInButton buttonSdkGoogleLogin;
    @BindView(R.id.button_sdk_twitter_login_sign_in)
    TwitterLoginButton buttonSdkTwitterLogin;
    @BindView(R.id.button_facebook_login_sign_in)
    Button buttonFacebookLogin;
    @BindView(R.id.button_google_login_sign_in)
    Button buttonGoogleLogin;
    @BindView(R.id.button_twitter_login_sign_in)
    Button buttonTwitterLogin;
    @BindView(R.id.button_login_sign_in)
    Button buttonLogin;
    @BindView(R.id.text_forgot_password_sign_in)
    TextView textForgotPassword;
    @BindView(R.id.text_sign_up_sign_in)
    TextView textSignUp;
    @BindView(R.id.edit_text_user_email_sign_in)
    AutoCompleteTextView editTextUserEmail;
    @BindView(R.id.edit_text_user_password_sign_in)
    EditText editTextUserPassword;
    @BindView(R.id.layout_forgot_password_sign_in)
    LinearLayout layout_forgot_password;
    @BindView(R.id.edit_text_user_email_forgot_password_sign_in)
    AutoCompleteTextView editTextForgotPasswordEmail;
    @BindView(R.id.button_forgot_password_proceed_sign_in)
    Button buttonForgotPasswordProceed;


    private Context mContext;
    private Activity mActivity;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private Author mLoggedAuthor;
    private TextView.OnEditorActionListener passwordEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                signInAction();
                return true;
            }
            return false;
        }
    };

    private TextView.OnEditorActionListener resetPasswordEmailListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (validateForgotPassword()) {
                    sendPasswordResetEmail();
                }
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));
        mContext = getApplicationContext();
        mActivity = SignInActivity.this;
        mAuth = FirebaseAuth.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            postSignInAction();
        }

        initListener();
        checkGetAccountsPermission();
        setLastLoginEmail();
        initFacebookAuth();
        initGoogleAuth();
        initTwitterAuth();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void setLastLoginEmail() {
        String lastLoginEmail = SharedManagement.getInstance(mContext).getString(SharedManagement.LAST_LOGIN_EMAIL);
        if (lastLoginEmail != null) {
            editTextUserEmail.setText(lastLoginEmail);
            editTextForgotPasswordEmail.setText(lastLoginEmail);
        }
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
                    // TODO show message
                }
                break;
            }
        }
    }

    private void initTwitterAuth() {

        buttonSdkTwitterLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                CommonView.getInstance(mContext).showDialog(
                        new CustomDialog().setActivity(mActivity)
                                .setDialogType(ERROR)
                                .setTitle(getString(R.string.error_exception))
                                .setMessage(exception.getMessage())
                );
            }
        });
    }


    private void initGoogleAuth() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void initFacebookAuth() {

        mCallbackManager = CallbackManager.Factory.create();
        buttonSdkFacebookLogin.setReadPermissions("email", "public_profile");
        buttonSdkFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // Do nothing
            }

            @Override
            public void onError(FacebookException error) {
                CommonView.getInstance(mContext).showDialog(
                        new CustomDialog().setActivity(mActivity)
                                .setDialogType(ERROR)
                                .setTitle(getString(R.string.error_exception))
                                .setMessage(error.getMessage())
                );
            }
        });
    }

    private void initListener() {

        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        buttonFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSdkFacebookLogin.performClick();
            }
        });

        buttonTwitterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSdkTwitterLogin.performClick();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAction();
            }
        });

        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(mActivity, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        editTextUserPassword.setOnEditorActionListener(passwordEditorActionListener);
        editTextForgotPasswordEmail.setOnEditorActionListener(resetPasswordEmailListener);


        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordLayout();
            }
        });

        layout_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideForgotPasswordLayout();
            }
        });

        buttonForgotPasswordProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForgotPassword()) {
                    sendPasswordResetEmail();
                }
            }
        });

        editTextUserEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextUserEmail.showDropDown();

            }
        });

        editTextForgotPasswordEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextForgotPasswordEmail.showDropDown();

            }
        });

    }

    private void addEmailAdapter() {

        Account[] accounts = AccountManager.get(this).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        for (Account account : accounts) {
            if ((account.name).matches(Constants.PATTERN_EMAIL)) {
                emailSet.add(account.name);
            }
        }
        editTextUserEmail.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));
        editTextForgotPasswordEmail.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));
    }

    private void sendPasswordResetEmail() {

        Utils.getInstance(mContext).hideSoftKeyboard(mActivity);
        hideForgotPasswordLayout();
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_reset_password_email), getString(R.string.text_please_wait));
        mAuth.sendPasswordResetEmail(editTextForgotPasswordEmail.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        if (task.isSuccessful()) {
                            showResetPasswordEmailSentSuccessDialog();
                        } else {
                            // If sign in fails, display a message to the user.
                            CommonView.getInstance(mContext).showDialog(
                                    new CustomDialog()
                                            .setActivity(mActivity)
                                            .setDialogType(ERROR)
                                            .setTitle(getString(R.string.error_exception))
                                            .setMessage(task.getException().getMessage()));
                        }
                    }
                });
    }

    private void showResetPasswordEmailSentSuccessDialog() {

        DialogBuilder builder = new DialogBuilder(mActivity);
        // Add the buttons
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setTitle(getString(R.string.msg_dialog_success));
        builder.setMessage(R.string.msg_reset_password_email_sent_success);
        builder.setDialogType(DialogBuilder.SUCCESS);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private boolean validateForgotPassword() {
        editTextForgotPasswordEmail.setError(null);

        String userEmail = editTextForgotPasswordEmail.getText().toString();

        if (userEmail.isEmpty()) {
            editTextForgotPasswordEmail.setError(getString(R.string.error_required));
            editTextForgotPasswordEmail.requestFocus();
            return false;
        }

        if (!userEmail.matches(Constants.PATTERN_EMAIL)) {
            editTextForgotPasswordEmail.setError(getString(R.string.error_invalid_email));
            editTextForgotPasswordEmail.requestFocus();
            return false;
        }
        return true;
    }

    private void showForgotPasswordLayout() {
        layout_forgot_password.setVisibility(View.VISIBLE);
        Animation bottomUp = AnimationUtils.loadAnimation(mContext, R.anim.bottom_up);
        layout_forgot_password.startAnimation(bottomUp);
        bottomUp.setDuration(Constants.ANIMATION_DELAY_SEC);

    }

    private void hideForgotPasswordLayout() {
        Utils.getInstance(mContext).hideSoftKeyboard(mActivity);
        layout_forgot_password.setVisibility(View.GONE);
        Animation bottomDown = AnimationUtils.loadAnimation(mContext, R.anim.bottom_down);
        layout_forgot_password.startAnimation(bottomDown);
        bottomDown.setDuration(Constants.ANIMATION_DELAY_SEC);
    }

    private void signInAction() {
        if (validateLogin()) {
            Utils.getInstance(mContext).hideSoftKeyboard(mActivity);
            CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_user_login), getString(R.string.text_please_wait));
            String email = editTextUserEmail.getText().toString();
            String password = editTextUserPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            CommonView.getInstance(mContext).dismissProgressDialog();
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                SharedManagement.getInstance(mContext).setString(SharedManagement.LAST_LOGIN_EMAIL, currentUser.getEmail());

                                if (currentUser.isEmailVerified()) {
                                    postSignInAction();
                                } else {

                                    DialogBuilder builder = new DialogBuilder(mActivity);
                                    // Add the buttons
                                    builder.setPositiveButton(R.string.action_verify_now, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            sendVerificationEmail();
                                        }
                                    });

                                    builder.setNegativeButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mAuth.signOut();
                                        }
                                    });

                                    builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                        }
                                    });
                                    // Set other dialog properties
                                    builder.setTitle(R.string.error_unverified_email);
                                    builder.setMessage(R.string.msg_unverified_email);
                                    builder.setDialogType(DialogBuilder.ERROR);

                                    // Create the AlertDialog
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            } else {
                                Toast.makeText(mActivity, getString(R.string.error_signin_failed), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void postSignInAction() {

        //TODO get user API

        mLoggedAuthor = new Gson().fromJson(Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_AUTHOR), Author.class);

        SharedManagement.getInstance(mContext).setLoggedUser(mLoggedAuthor);

        if (mLoggedAuthor.getCountry().getCountryId() == null) {
            Intent chooseCountryIntent = new Intent(mActivity, ChooseCountryActivity.class);
            startActivity(chooseCountryIntent);
        } else {
            Intent homeIntent = new Intent(mActivity, HomeActivity.class);
            startActivity(homeIntent);
        }


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
                            CommonView.getInstance(mContext).showDialog(
                                    new CustomDialog()
                                            .setActivity(mActivity)
                                            .setDialogType(SUCCESS)
                                            .setTitle(getString(R.string.success_email_sent))
                                            .setMessage(getString(R.string.msg_signup_success))
                            );
                        } else {
                            Toast.makeText(mActivity, getString(R.string.error_exception), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private boolean validateLogin() {

        editTextUserEmail.setError(null);
        editTextUserPassword.setError(null);

        String userEmail = editTextUserEmail.getText().toString();
        String userPassword = editTextUserPassword.getText().toString();

        if (userEmail.isEmpty()) {
            editTextUserEmail.setError(getString(R.string.error_required));
            editTextUserEmail.requestFocus();
            return false;
        }

        if (!userEmail.matches(Constants.PATTERN_EMAIL)) {
            editTextUserEmail.setError(getString(R.string.error_invalid_email));
            editTextUserEmail.requestFocus();
            return false;
        }
        if (userPassword.isEmpty()) {
            editTextUserPassword.setError(getString(R.string.error_required));
            editTextUserPassword.requestFocus();
            return false;
        }
        return true;
    }


    private void handleTwitterSession(TwitterSession session) {

        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_user_login), getString(R.string.text_please_wait));

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                // User is signed in.
                                addSocialLoginUser();
                            } else {
                                Toast.makeText(mActivity, getString(R.string.error_exception), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
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

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void authWithGoogle(GoogleSignInAccount acct) {

        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_user_login), getString(R.string.text_please_wait));

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                // User is signed in.
                                addSocialLoginUser();
                            } else {
                                Toast.makeText(mActivity, getString(R.string.error_exception), Toast.LENGTH_LONG).show();
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

    private void handleFacebookAccessToken(AccessToken token) {
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_user_login), getString(R.string.text_please_wait));
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                // User is signed in.
                                addSocialLoginUser();
                            } else {
                                Toast.makeText(mActivity, getString(R.string.error_exception), Toast.LENGTH_LONG).show();
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

    private void addSocialLoginUser() {

        Author author = new Author();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        author.setFirebaseId(currentUser.getUid());
        author.setName(currentUser.getDisplayName());
        author.setEmail(currentUser.getEmail());
        author.setProfileImage(currentUser.getPhotoUrl() == null ? null : currentUser.getPhotoUrl().toString());

        String authorData = new Gson().toJson(author);

        // TODO Call API to add or update user

        postSignInAction();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        buttonSdkTwitterLogin.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                authWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                CommonView.getInstance(mContext).showDialog(
                        new CustomDialog().setActivity(mActivity)
                                .setDialogType(ERROR)
                                .setTitle(getString(R.string.error_exception))
                                .setMessage(result.getStatus().getStatusMessage())
                );
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
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
