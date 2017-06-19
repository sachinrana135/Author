package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.alfanse.author.Adapters.FollowersAdapter;
import com.alfanse.author.Models.Author;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_FOLLOWERS;

public class FollowersActivity extends BaseActivity {

    @BindView(R.id.toolbar_followers)
    Toolbar mToolbar;
    @BindView(R.id.rv_followers_followers)
    RecyclerView recyclerViewFollowers;

    private Context mContext;
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private ArrayList<Author> mListFollowers;
    private FollowersAdapter mFollowersAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = FollowersActivity.this;
        mAuth = FirebaseAuth.getInstance();

        mListFollowers = new ArrayList<Author>();

        initToolbar();
        getFollowers();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_followers);
    }


    private void getFollowers() {

        String followersJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_FOLLOWERS);

        Type authorListType = new TypeToken<ArrayList<Author>>() {
        }.getType();
        mListFollowers = new Gson().fromJson(followersJson, authorListType);

        mFollowersAdapter = new FollowersAdapter(mContext, mListFollowers, new FollowersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Author follower) {

                String authorData = new Gson().toJson(follower);

            }
        });

        mLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerViewFollowers.setLayoutManager(mLinearLayoutManager);
        recyclerViewFollowers.setAdapter(mFollowersAdapter);

    }
}
