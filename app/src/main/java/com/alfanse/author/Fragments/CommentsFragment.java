package com.alfanse.author.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alfanse.author.Activities.AuthorActivity;
import com.alfanse.author.Adapters.CommentsAdapter;
import com.alfanse.author.Interfaces.onCommentItemClickListener;
import com.alfanse.author.Interfaces.onReportItemSubmitListener;
import com.alfanse.author.Models.Comment;
import com.alfanse.author.Models.ReportReason;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_COMMENTS;
import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_REPORTS;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_AUTHOR_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    @BindView(R.id.rv_comments_fragment_comments)
    RecyclerView recyclerViewComments;
    private LinearLayoutManager mLinearLayoutManager;

    private Context mContext;
    private Activity mActivity;
    private ArrayList<Comment> mListComments;
    private CommentsAdapter mCommentsAdapter;
    private String quoteId;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListComments = new ArrayList<Comment>();

        String commentsJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_COMMENTS);

        Type commentListType = new TypeToken<ArrayList<Comment>>() {
        }.getType();
        mListComments = new Gson().fromJson(commentsJson, commentListType);

        mCommentsAdapter = new CommentsAdapter(mContext, mListComments, new onCommentItemClickListener() {

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
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.bind(this, view);

        mLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerViewComments.setLayoutManager(mLinearLayoutManager);
        recyclerViewComments.setAdapter(mCommentsAdapter);
        return view;
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
