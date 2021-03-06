/*
 * Copyright (c) 2017. Alfanse Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.alfanse.author.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanse.author.Activities.AuthorActivity;
import com.alfanse.author.Activities.AuthorsActivity;
import com.alfanse.author.Activities.CommentsActivity;
import com.alfanse.author.Activities.QuoteActivity;
import com.alfanse.author.Adapters.QuotesAdapter;
import com.alfanse.author.CustomViews.DialogBuilder;
import com.alfanse.author.Interfaces.ExceptionDialogButtonListener;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Interfaces.UpdatableFragment;
import com.alfanse.author.Interfaces.bitmapRequestListener;
import com.alfanse.author.Interfaces.onAuthorFollowedListener;
import com.alfanse.author.Interfaces.onQuoteItemClickListener;
import com.alfanse.author.Interfaces.onQuoteLikedListener;
import com.alfanse.author.Interfaces.onReportItemSubmitListener;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.AuthorFilters;
import com.alfanse.author.Models.CommentFilters;
import com.alfanse.author.Models.CustomDialog;
import com.alfanse.author.Models.Quote;
import com.alfanse.author.Models.QuoteFilters;
import com.alfanse.author.Models.ReportReason;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.EndlessRecyclerViewScrollListener;
import com.alfanse.author.Utilities.SharedManagement;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.CustomViews.DialogBuilder.SUCCESS;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_AUTHOR_ID;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuotesFragment extends BaseFragment implements UpdatableFragment {

    private static final int DOWNLOAD_QUOTE_PERMISSION_REQUEST_CODE = 4704;
    private static final int SAVE_TEMP_QUOTE_PERMISSION_REQUEST_CODE = 7847;
    @BindView(R.id.swipe_refresh_fragment_quote)
    SwipeRefreshLayout layoutSwipeRefresh;
    @BindView(R.id.rv_quotes_fragment_quotes)
    RecyclerView recyclerViewQuotes;
    @BindView(R.id.empty_view_fragment_quotes)
    TextView emptyView;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Quote> mListQuotes;
    private QuotesAdapter mQuotesAdapter;
    private Quote quoteForDownload;
    private Bitmap quoteBitmapToSave = null;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private int mFirstPage = 1;
    private int mVisibleThreshold = 5;
    private QuoteFilters quoteFilters = new QuoteFilters();
    private Quote activeQuote = null;
    private Author mLoggedAuthor;
    private OnFragmentInteractionListener mListener;

    private onQuoteItemClickListener mOnQuoteItemClickListener = new onQuoteItemClickListener() {

        @Override
        public void onItemClick(Quote quote) {
            // Nothing to do
        }

        @Override
        public void onQuoteClick(Quote quote, ImageView quoteImage, ImageView authorImage) {

            Intent quoteIntent = new Intent(mActivity, QuoteActivity.class);
            Pair<View, String> p1 = Pair.create((View) quoteImage, getString(R.string.quote_image_shared_element_name));
            Pair<View, String> p2 = Pair.create((View) authorImage, getString(R.string.author_image_shared_element_name));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, p1, p2);
            quoteIntent.putExtra(BUNDLE_KEY_QUOTE_ID, quote.getId());
            startActivity(quoteIntent, options.toBundle());
        }

        @Override
        public void onActionLikeClick(Quote quote, onQuoteLikedListener listener) {
            activeQuote = quote;
            likeQuote(listener);
        }

        @Override
        public void onTotalLikesClick(Quote quote) {
            Intent authorsIntent = new Intent(mActivity, AuthorsActivity.class);
            authorsIntent.putExtra(Constants.BUNDLE_KEY_TITLE, getString(R.string.text_quote_liked_by));

            AuthorFilters authorFilters = new AuthorFilters();
            authorFilters.setQuoteID(quote.getId());
            authorFilters.setFilterType(Constants.AUTHOR_FILTER_TYPE_QUOTE_LIKED_BY);
            authorsIntent.putExtra(Constants.BUNDLE_KEY_AUTHORS_FILTERS, authorFilters);
            startActivity(authorsIntent);
        }

        @Override
        public void onActionCommentClick(Quote quote) {
            Intent commentIntent = new Intent(mActivity, CommentsActivity.class);
            CommentFilters commentFilters = new CommentFilters();
            commentFilters.setQuoteID(quote.getId());
            commentIntent.putExtra(Constants.BUNDLE_KEY_COMMENTS_FILTERS, commentFilters);
            startActivity(commentIntent);
        }

        @Override
        public void onActionShareClick(final Quote quote) {

            CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading), null);

            Utils.getInstance(mContext).getBitmapFromUrl(quote.getImageUrl(), new bitmapRequestListener() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    CommonView.getInstance(mContext).dismissProgressDialog();
                    if (bitmap != null) {

                        quoteBitmapToSave = bitmap;

                        checkPermissionAndSaveBitmapToDisk(bitmap);
                    }
                }

                @Override
                public void onError(Exception e) {
                    CommonView.getInstance(mContext).dismissProgressDialog();
                }
            });
        }

        @Override
        public void onActionFollowClick(Quote quote, onAuthorFollowedListener listener) {
            followAuthor(quote, listener);
        }

        @Override
        public void onActionDownloadClick(Quote quote) {
            quoteForDownload = quote;
            checkPermissionAndDownloadQuote(quote);
        }

        @Override
        public void onActionReportClick(Quote quote) {

            activeQuote = quote;
            getReportReasonsAndShowDialog();
        }

        @Override
        public void onActionDeleteClick(Quote quote) {
            showDeleteWarningDialog(quote);
        }

        @Override
        public void onAuthorClick(Quote quote) {
            Intent authorIntent = new Intent(mActivity, AuthorActivity.class);
            authorIntent.putExtra(BUNDLE_KEY_AUTHOR_ID, quote.getAuthor().getId());
            startActivity(authorIntent);
        }
    };

    private void showDeleteWarningDialog(final Quote quote) {

        DialogBuilder builder = new DialogBuilder(mActivity);
        // Add the buttons
        builder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteQuote(quote);
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.msg_quote_delete_confirm);
        builder.setDialogType(DialogBuilder.WARNING);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public QuotesFragment() {
        // Required empty public constructor
    }

    private void likeQuote(final onQuoteLikedListener listener) {
        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_QUOTE_ID, activeQuote.getId());

        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_LIKE_QUOTE)
                .setParams(param)
                .setMessage("QuotesFragment.java|likeQuote")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        listener.onQuoteLiked();
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        // Do nothing
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void followAuthor(Quote quote, final onAuthorFollowedListener listener) {
        //region API_CALL_START
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_please_wait), null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_LOGGED_AUTHOR_ID, mLoggedAuthor.getId());
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, quote.getAuthor().getId());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_FOLLOW_AUTHOR)
                .setParams(param)
                .setMessage("QuotesFragment.java|followAuthor")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                        listener.onAuthorFollowed();
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void getReportReasonsAndShowDialog() {
        //region API_CALL_START
        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity, null);
        HashMap<String, String> param = new HashMap<>();
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_REPORT_REASONS)
                .setParams(param)
                .setShowError(false)
                .setMessage("QuotesFragment.java|getReportReasonsAndShowDialog")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseGetReportReasonsResponse(stringResponse);
                        } catch (Exception e) {
                            Utils.getInstance(mContext).logException(e);
                        }
                        CommonView.getInstance(mContext).dismissProgressDialog();
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        CommonView.getInstance(mContext).showExceptionErrorDialog(mActivity, Utils.getInstance(mContext).getErrorMessage(e), new ExceptionDialogButtonListener() {
                            @Override
                            public void onRetryClick() {
                                getReportReasonsAndShowDialog();
                            }

                            @Override
                            public void onCancelClick() {
                                mActivity.onBackPressed();
                            }
                        });
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void parseGetReportReasonsResponse(String stringResponse) {

        //String reportReasonsJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_REPORTS);

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
                submitQuoteReport(reportId);
            }
        });
    }

    private void deleteQuote(final Quote quote) {
        //region API_CALL_START
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_please_wait), null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_AUTHOR_ID, mLoggedAuthor.getId());
        param.put(Constants.API_PARAM_KEY_QUOTE_ID, quote.getId());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_DELETE_QUOTE)
                .setParams(param)
                .setMessage("QuotesFragment.java|deleteQuote")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            CommonView.getInstance(mContext).dismissProgressDialog();
                            CommonView.showToast(mActivity, getString(R.string.success_quote_deleted), Toast.LENGTH_LONG, CommonView.ToastType.SUCCESS);
                            mListQuotes.remove(quote);
                            mQuotesAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Utils.getInstance(mContext).logException(e);
                        }
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

    private void submitQuoteReport(String reportId) {
        //region API_CALL_START
        CommonView.getInstance(mContext).showProgressDialog(mActivity, getString(R.string.text_loading_submitting_report), null);
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_REPORT_ID, reportId);
        param.put(Constants.API_PARAM_KEY_QUOTE_ID, activeQuote.getId());
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_REPORT_QUOTE)
                .setParams(param)
                .setMessage("QuotesFragment.java|submitQuoteReport")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseSubmitReportResponse(stringResponse);
                        } catch (Exception e) {
                            Utils.getInstance(mContext).logException(e);
                        }
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

        mListQuotes.remove(activeQuote);
        mQuotesAdapter.notifyDataSetChanged();

        CommonView.getInstance(mContext).showDialog(
                new CustomDialog().setActivity(mActivity)
                        .setDialogType(SUCCESS)
                        .setTitle(getString(R.string.success_quote_reported))
                        .setMessage(getString(R.string.msg_post_quote_report_submit))
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListQuotes = new ArrayList<Quote>();
        mQuotesAdapter = new QuotesAdapter(mContext, mListQuotes, mOnQuoteItemClickListener);
        mLoggedAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        ButterKnife.bind(this, view);

        int column = mContext.getResources().getInteger(R.integer.StaggeredGridLayoutManagerColumn);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewQuotes.setLayoutManager(layoutManager);
        recyclerViewQuotes.setAdapter(mQuotesAdapter);

        layoutSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListQuotes.clear();
                mQuotesAdapter.notifyDataSetChanged();
                loadQuotes(mFirstPage);
            }
        });

        loadQuotes(mFirstPage);

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadQuotes(page);
            }
        };

        mScrollListener.setVisibleThreshold(mVisibleThreshold);
        // Adds the scroll listener to RecyclerView
        recyclerViewQuotes.addOnScrollListener(mScrollListener);
        return view;
    }

    private void loadQuotes(int page) {

        layoutSwipeRefresh.setRefreshing(true);

        quoteFilters.setPage(Integer.toString(page));

        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_QUOTE_FILTERS, new Gson().toJson(quoteFilters));
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_QUOTES)
                .setParams(param)
                .setMessage("QuotesFragment.java|loadQuotes")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseLoadQuotesResponse(stringResponse);
                        } catch (Exception e) {
                            Utils.getInstance(mContext).logException(e);
                        }
                        layoutSwipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                        layoutSwipeRefresh.setRefreshing(false);
                        handleError(e);
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void parseLoadQuotesResponse(String stringResponse) {

        Type quoteListType = new TypeToken<ArrayList<Quote>>() {
        }.getType();

        ArrayList<Quote> listQuotes = new ArrayList<Quote>();
        listQuotes = new Gson().fromJson(stringResponse, quoteListType);

        mListQuotes.addAll(listQuotes);

        mQuotesAdapter.notifyDataSetChanged();

        if (quoteFilters.getPage() == Integer.toString(mFirstPage)) {

            if (mListQuotes.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerViewQuotes.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerViewQuotes.setVisibility(View.VISIBLE);
            }

            if (mListener != null) {
                if (mListQuotes.size() > 0) {
                    mListener.quotesAvailable(true);
                } else {
                    mListener.quotesAvailable(false);
                }
            }
        }
    }

    private void checkPermissionAndSaveBitmapToDisk(Bitmap quoteBitmap) {

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!Utils.getInstance(mContext).hasPermissions(PERMISSIONS)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            requestPermissions(PERMISSIONS, SAVE_TEMP_QUOTE_PERMISSION_REQUEST_CODE);
        } else {
            Uri uri = Utils.getInstance(mContext).saveBitmapToDisk(quoteBitmap);
            openImageShareIntent(uri);
        }
    }

    private void openImageShareIntent(Uri quoteImageUri) {

        if (quoteImageUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, quoteImageUri);
            shareIntent.setType("image/jpeg");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, getString(R.string.text_share_quote)));
        } else {
            // ...sharing failed, handle error
        }
    }

    private void checkPermissionAndDownloadQuote(Quote quote) {

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!Utils.getInstance(mContext).hasPermissions(PERMISSIONS)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            requestPermissions(PERMISSIONS, DOWNLOAD_QUOTE_PERMISSION_REQUEST_CODE);
        } else {
            Utils.getInstance(mContext).downloadImageToDisk(quote.getOriginalImageUrl());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }

        mContext = context;
        mActivity = getActivity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        }
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case DOWNLOAD_QUOTE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utils.getInstance(mContext).downloadImageToDisk(quoteForDownload.getOriginalImageUrl());
                } else {
                    CommonView.showToast(mActivity, getString(R.string.warning_permission_denied), Toast.LENGTH_LONG, CommonView.ToastType.WARNING);
                }
                break;
            }
            case SAVE_TEMP_QUOTE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = Utils.getInstance(mContext).saveBitmapToDisk(quoteBitmapToSave);
                    openImageShareIntent(uri);
                } else {
                    CommonView.showToast(mActivity, getString(R.string.warning_permission_denied), Toast.LENGTH_LONG, CommonView.ToastType.WARNING);
                }
                break;
            }
        }
    }

    public QuoteFilters getQuoteFilters() {
        return quoteFilters;
    }

    public void setQuoteFilters(QuoteFilters quoteFilters) {
        this.quoteFilters = quoteFilters;
    }

    @Override
    public void update() {
        mListQuotes.clear();
        mQuotesAdapter.notifyDataSetChanged();
        loadQuotes(mFirstPage);
    }

    private void handleError(Exception e) {
        CommonView.getInstance(mContext).showExceptionErrorDialog(mActivity, Utils.getInstance(mContext).getErrorMessage(e), new ExceptionDialogButtonListener() {
            @Override
            public void onRetryClick() {
                loadQuotes(mFirstPage);
            }

            @Override
            public void onCancelClick() {
                mActivity.onBackPressed();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void quotesAvailable(Boolean quotesAvailable);
    }

}
