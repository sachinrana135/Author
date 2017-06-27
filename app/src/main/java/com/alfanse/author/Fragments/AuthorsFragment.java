package com.alfanse.author.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alfanse.author.Activities.AuthorActivity;
import com.alfanse.author.Adapters.AuthorsAdapter;
import com.alfanse.author.Interfaces.onAuthorItemClickListener;
import com.alfanse.author.Models.Author;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.EndlessRecyclerViewScrollListener;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_AUTHORS;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_AUTHOR_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthorsFragment extends Fragment {


    @BindView(R.id.rv_authors_fragment_authors)
    RecyclerView recyclerViewAuthors;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Author> mListAuthors;
    private AuthorsAdapter mAuthorsAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private int mFirstPage = 1;
    private int mVisibleThreshold = 7;

    private onAuthorItemClickListener mOnAuthorItemClickListener = new onAuthorItemClickListener() {
        @Override
        public void onItemClick(Author author) {

            String authorData = new Gson().toJson(author);

        }

        @Override
        public void onActionFollowClick(Author author) {
            String authorData = new Gson().toJson(author);
        }

        @Override
        public void onAuthorClick(Author author) {
            Intent authorIntent = new Intent(mActivity, AuthorActivity.class);
            authorIntent.putExtra(BUNDLE_KEY_AUTHOR_ID, author.getId());
            startActivity(authorIntent);
        }
    };

    public AuthorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAuthors = new ArrayList<Author>();
        mAuthorsAdapter = new AuthorsAdapter(mContext, mListAuthors, mOnAuthorItemClickListener);
        loadMoreAuthors(mFirstPage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authors, container, false);
        ButterKnife.bind(this, view);

        int column = 1;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewAuthors.setLayoutManager(layoutManager);
        recyclerViewAuthors.setAdapter(mAuthorsAdapter);
        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreAuthors(page);
            }
        };
        mScrollListener.setVisibleThreshold(mVisibleThreshold);
        // Adds the scroll listener to RecyclerView
        recyclerViewAuthors.addOnScrollListener(mScrollListener);
        return view;
    }

    private void loadMoreAuthors(int mFirstPage) {

        String followersJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_AUTHORS);

        Type authorListType = new TypeToken<ArrayList<Author>>() {
        }.getType();

        ArrayList<Author> listAuthors = new ArrayList<>();
        listAuthors = new Gson().fromJson(followersJson, authorListType);

        mListAuthors.addAll(listAuthors);

        mAuthorsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
