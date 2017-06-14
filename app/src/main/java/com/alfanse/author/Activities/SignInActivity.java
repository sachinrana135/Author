package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 2545;
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
    EditText editTextUserEmail;
    @BindView(R.id.edit_text_user_password_sign_in)
    EditText editTextUserPassword;


    private Context mContext;
    private Activity mActivity;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

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
            //Intent homeIntent = new Intent(mActivity,HomeActivity.class);
            Intent homeIntent = new Intent(mActivity, PublishQuoteActivity.class);
            startActivity(homeIntent);
        }

        initListener();
        initFacebookAuth();
        initGoogleAuth();
        initTwitterAuth();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initTwitterAuth() {

        buttonSdkTwitterLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                CommonView.getInstance(mContext).showErrorDialog(mActivity, null, exception.getMessage());
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
                CommonView.getInstance(mContext).showErrorDialog(mActivity, null, error.getMessage());
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
                if (validateLogin()) {

                    String email = editTextUserEmail.getText().toString();
                    String password = editTextUserPassword.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                    } else {
                                        CommonView.getInstance(mContext).showErrorDialog(mActivity, null, task.getException().getMessage());
                                    }
                                }
                            });
                }
            }
        });

        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(mActivity, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPasswordIntent = new Intent(mActivity, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
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

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            addOrUpdateUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            CommonView.getInstance(mContext).showErrorDialog(mActivity, null, task.getException().getMessage());
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void authWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            addOrUpdateUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            CommonView.getInstance(mContext).showErrorDialog(mActivity, null, task.getException().getMessage());
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // User is signed in.
                                addOrUpdateUser();
                            } else {
                                // No user is signed in.
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            CommonView.getInstance(mContext).showErrorDialog(mActivity, null, task.getException().getMessage());
                        }

                    }
                });
    }

    private void addOrUpdateUser() {

        /*FirebaseUser author = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String,String> authorData= new HashMap<String, String>();
        authorData.put(Author.AUTHOR_NAME, author.getDisplayName());
        authorData.put(Author.AUTHOR_EMAIL, author.getEmail());
        authorData.put(Author.AUTHOR_PROFILE_IMAGE, author.getPhotoUrl().toString());
        authorData.put(Author.AUTHOR_PROVIDER_DATA, author.getProviderData().toString());
        authorData.put(Author.AUTHOR_PROVIDER_ID, author.getProviderId());
        authorData.put(Author.AUTHOR_PROVIDERS, author.getProviders().toString());
        authorData.put(Author.AUTHOR_COUNTRY, Utils.getInstance(mContext).getCurrentLocale().getCountry());
        authorData.put(Author.AUTHOR_LANGUAGE, Utils.getInstance(mContext).getCurrentLocale().getLanguage());*/

        // TODO Call API to add or update user
        // TODO call API if user has complete details, if not, redirect to complete details activity

        Intent homeIntent = new Intent(mActivity, HomeActivity.class);
        startActivity(homeIntent);

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
                CommonView.getInstance(mContext).showErrorDialog(mActivity, null, result.getStatus().getStatusMessage());
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
