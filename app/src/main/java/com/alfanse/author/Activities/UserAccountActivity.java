package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.alfanse.author.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAccountActivity extends BaseActivity {

    @BindView(R.id.toolbar_account_user)
    Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private Activity mActivity;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_user);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mActivity = UserAccountActivity.this;
        mAuth = FirebaseAuth.getInstance();

        initToolbar();
        initListener();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    private void initListener() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            
            case R.id.action_signout_account_user:
                mAuth.signOut();
                Intent signInIntent = new Intent(mActivity, SignInActivity.class);
                startActivity(signInIntent);
                finish();
            case R.id.action_update_password_account_user:

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(menuItem);

        }
    }
}
