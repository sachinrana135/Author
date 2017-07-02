package com.alfanse.author.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alfanse.author.Activities.AuthorActivity;
import com.alfanse.author.Adapters.CommentsAdapter;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Interfaces.onCommentItemClickListener;
import com.alfanse.author.Interfaces.onReportItemSubmitListener;
import com.alfanse.author.Models.Comment;
import com.alfanse.author.Models.CommentFilters;
import com.alfanse.author.Models.ReportReason;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.EndlessRecyclerViewScrollListener;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_REPORTS;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_AUTHOR_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    @BindView(R.id.swipe_refresh_fragment_comments)
    SwipeRefreshLayout layoutSwipeRefresh;
    @BindView(R.id.rv_comments_fragment_comments)
    RecyclerView recyclerViewComments;
    @BindView(R.id.edit_text_enter_comment_fragment_comments)
    EditText editTextEnterComment;
    @BindView(R.id.fab_submit_comment_fragment_comment)
    FloatingActionButton fabSubmitComment;


    private Context mContext;
    private Activity mActivity;
    private ArrayList<Comment> mListComments;
    private CommentsAdapter mCommentsAdapter;
    private int mFirstPage = 1;
    private int mVisibleThreshold = 10;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private CommentFilters commentFilters = new CommentFilters();
    private onCommentItemClickListener mOnCommentItemClickListener = new onCommentItemClickListener() {

        @Override
        public void onActionReportClick(Comment comment) {

            String reportReasonsJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_REPORTS);

            Type reportListType = new TypeToken<ArrayList<ReportReason>>() {
            }.getType();

            ArrayList<ReportReason> listReportReasons = new Gson().fromJson(reportReasonsJson, reportListType);

            final HashMap<String, String> hashReportReasons = new HashMap<String, String>();
            ArrayList<String> listReportReasonsTitle = new ArrayList<String>();
            for (ReportReason reportReason : listReportReasons) {
                hashReportReasons.put(reportReason.getTitle(), reportReason.getId());
                listReportReasonsTitle.add(reportReason.getTitle());
            }
            CommonView.getInstance(mContext).showReportDialog(listReportReasonsTitle, mActivity, new onReportItemSubmitListener() {
                @Override
                public void onReportItemSubmit(String titleReport) {

                    String reportId = hashReportReasons.get(titleReport);

                }
            });


        }

        @Override
        public void onAuthorClick(Comment comment) {

            Intent authorIntent = new Intent(mActivity, AuthorActivity.class);
            authorIntent.putExtra(BUNDLE_KEY_AUTHOR_ID, comment.getAuthor().getId());
            startActivity(authorIntent);
        }
    };
    private View.OnClickListener commentSubmitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!editTextEnterComment.getText().toString().trim().equalsIgnoreCase(null)
                    && !editTextEnterComment.getText().toString().trim().equalsIgnoreCase("")
                    && !editTextEnterComment.getText().toString().trim().isEmpty()) {
                // TODO Submit comment API
            }

        }
    };

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListComments = new ArrayList<Comment>();
        mCommentsAdapter = new CommentsAdapter(mContext, mListComments, mOnCommentItemClickListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.bind(this, view);

        int column = 1;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewComments.setLayoutManager(layoutManager);
        recyclerViewComments.setAdapter(mCommentsAdapter);

        layoutSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListComments.clear();
                mCommentsAdapter.notifyDataSetChanged();
                loadComments(mFirstPage);
            }
        });

        loadComments(mFirstPage);

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadComments(page);
            }
        };
        mScrollListener.setVisibleThreshold(mVisibleThreshold);
        // Adds the scroll listener to RecyclerView
        recyclerViewComments.addOnScrollListener(mScrollListener);
        return view;
    }

    private void loadComments(int page) {

        layoutSwipeRefresh.setRefreshing(true);

        commentFilters.setPage(Integer.toString(page));

        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_COMMENT_FILTERS, new Gson().toJson(commentFilters));
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_COMMENTS)
                .setParams(param)
                .setMessage("CommentsFragment.java|loadComments")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        parseLoadCommentsResponse(stringResponse);
                        layoutSwipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        layoutSwipeRefresh.setRefreshing(false);
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void parseLoadCommentsResponse(String stringResponse) {

        // String commentsJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_COMMENTS);

        Type commentListType = new TypeToken<ArrayList<Comment>>() {
        }.getType();

        ArrayList<Comment> listComments = new ArrayList<>();

        listComments = new Gson().fromJson(stringResponse, commentListType);
        mListComments.addAll(listComments);
        mCommentsAdapter.notifyDataSetChanged();
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

    public CommentFilters getCommentFilters() {
        return commentFilters;
    }

    public void setCommentFilters(CommentFilters commentFilters) {
        this.commentFilters = commentFilters;
    }
}
