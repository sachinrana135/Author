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
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.Comment;
import com.alfanse.author.Models.CommentFilters;
import com.alfanse.author.Models.CustomDialog;
import com.alfanse.author.Models.ReportReason;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.EndlessRecyclerViewScrollListener;
import com.alfanse.author.Utilities.SharedManagement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.CustomViews.DialogBuilder.SUCCESS;
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
    private Comment activeComment = null;
    private Author mLoggedAuthor;
    private onCommentItemClickListener mOnCommentItemClickListener = new onCommentItemClickListener() {

        @Override
        public void onActionReportClick(Comment comment) {
            activeComment = comment;
            getReportReasonsAndShowDialog();
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
                saveComment();
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
        mLoggedAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
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
        fabSubmitComment.setOnClickListener(commentSubmitOnClickListener);

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

    private void getReportReasonsAndShowDialog() {
        //region API_CALL_START
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, null);
        HashMap<String, String> param = new HashMap<>();
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_REPORT_REASONS)
                .setParams(param)
                .setMessage("CommentsFragment.java|getReportReasonsAndShowDialog")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        parseGetReportReasonsResponse(stringResponse);
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void parseGetReportReasonsResponse(String stringResponse) {

        //  String reportReasonsJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_REPORTS);

        Type reportListType = new TypeToken<ArrayList<ReportReason>>() {
        }.getType();

        ArrayList<ReportReason> listReportReasons = new Gson().fromJson(stringResponse, reportListType);

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
                submitCommentReport(reportId);

            }
        });

    }

    private void submitCommentReport(String reportId) {
        //region API_CALL_START
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_submitting_report), null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_REPORT_ID, reportId);
        param.put(Constants.API_PARAM_KEY_COMMENT_ID, activeComment.getId());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_REPORT_COMMENT)
                .setParams(param)
                .setMessage("CommentsFragment.java|submitCommentReport")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        parseSubmitReportResponse(stringResponse);
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void parseSubmitReportResponse(String stringResponse) {

        CommonView.getInstance(mContext).showDialog(
                new CustomDialog().setActivity(mActivity)
                        .setDialogType(SUCCESS)
                        .setTitle(getString(R.string.success_comment_reported))
                        .setMessage(getString(R.string.msg_post_comment_report_submit))
        );
    }

    private void saveComment() {

        //region API_CALL_START
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_save_comment), null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, mLoggedAuthor.getId());
        param.put(Constants.API_PARAM_KEY_QUOTE_ID, commentFilters.getQuoteID());
        param.put(Constants.API_PARAM_KEY_COMMENT, editTextEnterComment.getText().toString().trim());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_SAVE_COMMENT)
                .setParams(param)
                .setMessage("CommentsFragment.java|saveComment")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        parseSaveCommentResponse(stringResponse);
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void parseSaveCommentResponse(String stringResponse) {

        loadComments(mFirstPage);
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
