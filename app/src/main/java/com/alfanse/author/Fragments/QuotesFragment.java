package com.alfanse.author.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alfanse.author.Activities.AuthorActivity;
import com.alfanse.author.Activities.CommentsActivity;
import com.alfanse.author.Activities.QuoteActivity;
import com.alfanse.author.Adapters.QuotesAdapter;
import com.alfanse.author.Interfaces.bitmapRequestListener;
import com.alfanse.author.Interfaces.onQuoteItemClickListener;
import com.alfanse.author.Interfaces.onReportItemSubmitListener;
import com.alfanse.author.Models.Quote;
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

import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_QUOTES;
import static com.alfanse.author.Utilities.Constants.ASSETS_FILE_REPORTS;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_AUTHOR_ID;
import static com.alfanse.author.Utilities.Constants.BUNDLE_KEY_QUOTE_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuotesFragment extends Fragment {

    private static final int DOWNLOAD_QUOTE_PERMISSION_REQUEST_CODE = 4704;
    private static final int SAVE_TEMP_QUOTE_PERMISSION_REQUEST_CODE = 7847;
    @BindView(R.id.rv_quotes_fragment_quotes)
    RecyclerView recyclerViewQuotes;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Quote> mListQuotes;
    private QuotesAdapter mQuotesAdapter;
    private Quote quoteForDownload;
    private Bitmap quoteBitmapToSave = null;

    public QuotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListQuotes = new ArrayList<Quote>();

        String quotesJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_QUOTES);

        Type quoteListType = new TypeToken<ArrayList<Quote>>() {
        }.getType();
        mListQuotes = new Gson().fromJson(quotesJson, quoteListType);

        mQuotesAdapter = new QuotesAdapter(mContext, mListQuotes, new onQuoteItemClickListener() {

            @Override
            public void onItemClick(Quote quote) {
                // Nothing to do
            }

            @Override
            public void onQuoteClick(Quote quote) {
                Intent quoteIntent = new Intent(mActivity, QuoteActivity.class);
                quoteIntent.putExtra(BUNDLE_KEY_QUOTE_ID, quote.getId());
                startActivity(quoteIntent);
            }

            @Override
            public void onActionLikeClick(Quote quote) {
                // Nothing to do, event handled in QuotesAdapter
            }

            @Override
            public void onActionCommentClick(Quote quote) {
                Intent commentIntent = new Intent(mActivity, CommentsActivity.class);
                commentIntent.putExtra(BUNDLE_KEY_QUOTE_ID, quote.getId());
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
            public void onActionFollowClick(Quote quote) {
                // Nothing to do, event handled in QuotesAdapter
            }

            @Override
            public void onActionDownloadClick(Quote quote) {
                quoteForDownload = quote;
                checkPermissionAndDownloadQuote(quote);
            }

            @Override
            public void onActionReportClick(Quote quote) {

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
            public void onAuthorClick(Quote quote) {
                Intent authorIntent = new Intent(mActivity, AuthorActivity.class);
                authorIntent.putExtra(BUNDLE_KEY_AUTHOR_ID, quote.getAuthor().getId());
                startActivity(authorIntent);
            }
        });

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
            Utils.getInstance(mContext).downloadImageToDisk(quote.getImageUrl());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        ButterKnife.bind(this, view);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerViewQuotes.setLayoutManager(new GridLayoutManager(mContext, 1));
        } else {
            recyclerViewQuotes.setLayoutManager(new GridLayoutManager(mContext, 2));
        }
        recyclerViewQuotes.setAdapter(mQuotesAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        mActivity = getActivity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
                    Utils.getInstance(mContext).downloadImageToDisk(quoteForDownload.getImageUrl());
                } else {
                    // TODO show message
                }
                break;
            }
            case SAVE_TEMP_QUOTE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = Utils.getInstance(mContext).saveBitmapToDisk(quoteBitmapToSave);
                    openImageShareIntent(uri);
                } else {
                    // TODO show message
                }
                break;
            }
        }
    }


}
