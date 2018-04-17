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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alfanse.author.Adapters.CanvasThemesAdapter;
import com.alfanse.author.Adapters.FilterAdapter;
import com.alfanse.author.CustomViews.ComponentBoxView;
import com.alfanse.author.CustomViews.ComponentImageView;
import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.QuoteCanvas;
import com.alfanse.author.Interfaces.NetworkCallback;
import com.alfanse.author.Interfaces.onCanvasThemeItemClickListener;
import com.alfanse.author.Interfaces.onFilterItemClickListener;
import com.alfanse.author.Models.CanvasTheme;
import com.alfanse.author.Models.Filter;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.ApiUtils;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.EndlessRecyclerViewScrollListener;
import com.alfanse.author.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import com.theartofdev.edmodo.cropper.CropImage;

import net.alhazmy13.imagefilter.ImageFilter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CanvasOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CanvasOptionsFragment extends BaseFragment implements ColorPickerDialogListener {

    public static final int CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID = 100;
    private static final int ALL_PERMISSIONS_REQUEST_CODE = 1000;
    private static final String IMAGE_REQUIRED_FOR_CANVAS = "IMAGE_REQUIRED_FOR_CANVAS";
    private static final String IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW = "IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW";

    @BindView(R.id.layout_colorize_fragment_canvas_options)
    LinearLayout optionColorize;
    @BindView(R.id.layout_gallery_fragment_canvas_options)
    LinearLayout optionGallery;
    @BindView(R.id.layout_filter_fragment_canvas_options)
    LinearLayout optionFilter;
    @BindView(R.id.layout_add_photo_fragment_canvas_options)
    LinearLayout optionAddImage;
    @BindView(R.id.layout_add_text_fragment_canvas_options)
    LinearLayout optionAddText;
    @BindView(R.id.layout_add_box_fragment_canvas_options)
    LinearLayout optionAddBox;
    @BindView(R.id.layout_canvas_themes_fragment_canvas_options)
    LinearLayout layoutCanvasThemes;
    @BindView(R.id.rv_canvas_themes_fragment_canvas_options)
    RecyclerView recyclerViewCanvasThemes;
    @BindView(R.id.layout_filters_list_fragment_canvas_options)
    LinearLayout layoutFilters;
    @BindView(R.id.rv_filters_fragment_canvas_options)
    RecyclerView recyclerViewFilters;

    private Context mContext;
    private Activity mActivity;
    private QuoteCanvas mCanvas;
    private Uri mCropImageUri;
    private CanvasThemesAdapter mCanvasThemesAdapter;
    private FilterAdapter mFiltersAdapter;
    private ArrayList<CanvasTheme> mListCanvasThemes = new ArrayList<CanvasTheme>();
    private ArrayList<Filter> mListFilters = new ArrayList<Filter>();
    private OnFragmentInteractionListener mListener;
    private ComponentTextView mActiveComponentTextView;
    private LinearLayoutManager mThemeLinearLayoutManager;
    private LinearLayoutManager mFilterLinearLayoutManager;
    private CanvasTheme defaultCanvasTheme;
    private int firstIndex = 0;
    private String imageRequiredFor = null;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private int mFirstPage = 1;
    private int mVisibleThreshold = 5;

    private onCanvasThemeItemClickListener mOnCanvasThemeItemClickListener = new onCanvasThemeItemClickListener() {
        @Override
        public void onItemClick(CanvasTheme canvasTheme) {
            mCanvas.setBackground(canvasTheme.getImageUrl());
            if (mCanvas.findViewWithTag(Constants.TAG_DEFAULT_CANVASE_TEXT_VIEW) != null) {
                ComponentTextView componentTextView = (ComponentTextView) mCanvas.findViewWithTag(Constants.TAG_DEFAULT_CANVASE_TEXT_VIEW);
                componentTextView.setTheme(canvasTheme);
            }
        }
    };

    private onFilterItemClickListener mOnFilterItemClickListener = new onFilterItemClickListener() {

        @Override
        public void onItemClick(Filter filter) {
            setActiveFilter(filter);
            mCanvas.setFilter(filter);
        }
    };

    public CanvasOptionsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListCanvasThemes = new ArrayList<CanvasTheme>();
        mCanvasThemesAdapter = new CanvasThemesAdapter(mContext, mListCanvasThemes, mOnCanvasThemeItemClickListener);

        mListFilters = new ArrayList<Filter>();
        mFiltersAdapter = new FilterAdapter(mContext, mListFilters, mOnFilterItemClickListener);

        loadFilters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_canvas_options, container, false);
        ButterKnife.bind(this, view);

        mThemeLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCanvasThemes.setLayoutManager(mThemeLinearLayoutManager);
        recyclerViewCanvasThemes.setAdapter(mCanvasThemesAdapter);

        mFilterLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFilters.setLayoutManager(mFilterLinearLayoutManager);
        recyclerViewFilters.setAdapter(mFiltersAdapter);

        loadThemes(mFirstPage);
        initListener();

        return view;
    }

    private void loadFilters() {

        mListFilters.clear();

        Filter originalFilter = new Filter(getString(R.string.original), null, true);
        mListFilters.add(originalFilter);
        mListFilters.add(new Filter(getString(R.string.gray), ImageFilter.Filter.GRAY, false));
        mListFilters.add(new Filter(getString(R.string.blur), ImageFilter.Filter.AVERAGE_BLUR, false));
        mListFilters.add(new Filter(getString(R.string.oil), ImageFilter.Filter.OIL, false));
        mListFilters.add(new Filter(getString(R.string.old_tv), ImageFilter.Filter.TV, false));
        mListFilters.add(new Filter(getString(R.string.invert), ImageFilter.Filter.INVERT, false));
        mListFilters.add(new Filter(getString(R.string.block), ImageFilter.Filter.BLOCK, false));
        mListFilters.add(new Filter(getString(R.string.old), ImageFilter.Filter.OLD, false));
        mListFilters.add(new Filter(getString(R.string.sharpen), ImageFilter.Filter.SHARPEN, false));
        mListFilters.add(new Filter(getString(R.string.light), ImageFilter.Filter.LIGHT, false));
        mListFilters.add(new Filter(getString(R.string.lomo), ImageFilter.Filter.LOMO, false));
        mListFilters.add(new Filter(getString(R.string.hdr), ImageFilter.Filter.HDR, false));
        mListFilters.add(new Filter(getString(R.string.gaussian), ImageFilter.Filter.GAUSSIAN_BLUR, false));
        mListFilters.add(new Filter(getString(R.string.soft), ImageFilter.Filter.SOFT_GLOW, false));
        mListFilters.add(new Filter(getString(R.string.sketch), ImageFilter.Filter.SKETCH, false));
        mListFilters.add(new Filter(getString(R.string.motion), ImageFilter.Filter.MOTION_BLUR, false));
        mListFilters.add(new Filter(getString(R.string.gotham), ImageFilter.Filter.GOTHAM, false));
        mListFilters.add(new Filter(getString(R.string.pixelate), ImageFilter.Filter.PIXELATE, false));
        mListFilters.add(new Filter(getString(R.string.neon), ImageFilter.Filter.NEON, false));
        mListFilters.add(new Filter(getString(R.string.relief), ImageFilter.Filter.RELIEF, false));

        setActiveFilter(originalFilter);
        mCanvas.setFilter(originalFilter);

        mFiltersAdapter.notifyDataSetChanged();

    }

    private void setActiveFilter(Filter filter) {
        for (Filter filter1 : mListFilters) {
            filter1.setSelected(false);
        }
        mListFilters.get(mListFilters.indexOf(filter)).setSelected(true);
        mFiltersAdapter.notifyDataSetChanged();
    }

    private void initListener() {

        initOptionItemClickListener();

        mScrollListener = new EndlessRecyclerViewScrollListener(mThemeLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadThemes(page);
            }
        };
        mScrollListener.setVisibleThreshold(mVisibleThreshold);
        // Adds the scroll listener to RecyclerView
        recyclerViewCanvasThemes.addOnScrollListener(mScrollListener);
    }

    private void loadThemes(int page) {

        //region API_CALL_START
        HashMap<String, String> param = new HashMap<>();
        param.put(Constants.API_PARAM_KEY_PAGE, Integer.toString(page));
        ApiUtils api = new ApiUtils(mContext)
                .setActivity(mActivity)
                .setUrl(Constants.API_URL_GET_CANVAS_THEMES)
                .setParams(param)
                .setShouldCache(true)
                .setMessage("CanvasOptionsFragment.java|loadThemes")
                .setStringResponseCallback(new NetworkCallback.stringResponseCallback() {
                    @Override
                    public void onSuccessCallBack(String stringResponse) {
                        try {
                            parseLoadThemesResponse(stringResponse);
                        } catch (Exception e) {
                            Utils.getInstance(mContext).logException(e);
                        }
                    }

                    @Override
                    public void onFailureCallBack(Exception e) {
                    }
                });

        api.call();
        //endregion API_CALL_END
    }

    private void parseLoadThemesResponse(String stringResponse) {
        //String themesJson = Utils.getInstance(mContext).getJsonResponse(ASSETS_FILE_CANVAS_THEMES);

        Type themeListType = new TypeToken<ArrayList<CanvasTheme>>() {
        }.getType();

        ArrayList<CanvasTheme> listThemes = new ArrayList<>();
        listThemes = new Gson().fromJson(stringResponse, themeListType);

        mListCanvasThemes.addAll(listThemes);

        mCanvasThemesAdapter.notifyDataSetChanged();
    }

    private void initOptionItemClickListener() {

        optionColorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowPresets(true)
                        .setDialogId(CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorBlack))
                        .setShowAlphaSlider(false)
                        .show(mActivity);
            }
        });

        optionGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRequiredFor = IMAGE_REQUIRED_FOR_CANVAS;
                startPickImageActivity();
            }
        });

        optionFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCanvasThemeVisible()) {
                    hideCanvasThemes();
                } else {
                    showCanvasThemes();
                }
            }
        });

        optionAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComponentTextView();
            }
        });

        optionAddBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComponentBoxView();
            }
        });

        optionAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRequiredFor = IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW;
                startPickImageActivity();
            }
        });

    }

    @SuppressLint("NewApi")
    private void startPickImageActivity() {

        // For API >= 23 we need to check specifically that we have permissions to read external storage,
        // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.

        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (!Utils.getInstance(mContext).hasPermissions(PERMISSIONS)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            requestPermissions(PERMISSIONS, ALL_PERMISSIONS_REQUEST_CODE);
        } else {
            startActivityForResult(CropImage.getPickImageChooserIntent(mContext), PICK_IMAGE_CHOOSER_REQUEST_CODE);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mContext = context;
        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setQuoteCanvas(QuoteCanvas quoteCanvas) {
        mCanvas = quoteCanvas;
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        mCanvas.setBackground(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void startCropImageActivity(Uri imageUri) {
        if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_CANVAS)) {
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(mContext, this); // Need square image
        } else if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW)) {
            CropImage.activity(imageUri).start(mContext, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(CropImage.getPickImageChooserIntent(mContext), PICK_IMAGE_CHOOSER_REQUEST_CODE);
                } else {
                    CommonView.showToast(mActivity, getString(R.string.warning_permission_denied), Toast.LENGTH_LONG, CommonView.ToastType.WARNING);
                }
                break;
            }
            case CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE: {
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startCropImageActivity(mCropImageUri);
                } else {
                    CommonView.showToast(mActivity, getString(R.string.warning_permission_denied), Toast.LENGTH_LONG, CommonView.ToastType.WARNING);
                }
                break;
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = CropImage.getPickImageResultUri(mActivity, data);

                    // For API >= 23 we need to check specifically that we have permissions to read external storage.
                    if (CropImage.isReadExternalStoragePermissionsRequired(mActivity, imageUri)) {
                        // request permissions and handle the result in onRequestPermissionsResult()
                        mCropImageUri = imageUri;
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                    } else {
                        // no permissions required or already grunted, can start crop image activity
                        startCropImageActivity(imageUri);
                    }
                }
                break;
            }

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK) {

                    if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_CANVAS)) {
                        Uri croppedImageUri = result.getUri();
                        mCanvas.setBackground(croppedImageUri);
                    } else if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW)) {
                        addComponentImageView(result.getUri());
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    CommonView.showToast(mActivity, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG, CommonView.ToastType.ERROR);
                }
                break;
            }
        }
    }

    private void addComponentTextView() {

        ComponentTextView textView = new ComponentTextView(mActivity, mCanvas);
        textView.setTheme(CanvasTheme.getDefaultTheme());

        textView.setMinimumHeight(textView.getMinimumHeight());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (mCanvas.getWidth() * .7), FrameLayout.LayoutParams.WRAP_CONTENT);

        mCanvas.addView(textView, layoutParams);

        if (mListener != null) {
            mListener.onComponentTextViewAdded(textView);
        }
    }

    private void addComponentBoxView() {

        ComponentBoxView boxView = new ComponentBoxView(mActivity, mCanvas);

        boxView.setMinimumHeight(boxView.getMinimumHeight());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mCanvas.getWidth() / 2, mCanvas.getHeight() / 2);

        mCanvas.addView(boxView, layoutParams);

        if (mListener != null) {
            mListener.onComponentBoxViewAdded(boxView);
        }
    }

    private void addComponentImageView(Uri imageUri) {

        ComponentImageView imageView = new ComponentImageView(mActivity, mCanvas, imageUri);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        mCanvas.addView(imageView, layoutParams);

        if (mListener != null) {
            mListener.onComponentImageViewAdded(imageView);
        }

    }

    public void showCanvasThemes() {
        layoutCanvasThemes.setVisibility(View.VISIBLE);
        layoutFilters.setVisibility(View.GONE);
    }

    public void hideCanvasThemes() {
        layoutCanvasThemes.setVisibility(View.GONE);
        layoutFilters.setVisibility(View.VISIBLE);
        int scrollPosition = mCanvas.getFilter() != null ? mListFilters.indexOf(mCanvas.getFilter()) : 0;
        if (scrollPosition < (mListFilters.size() - 1)) {
            scrollPosition = scrollPosition + 1;
        }
        final int finalScrollPosition = scrollPosition;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                recyclerViewFilters.smoothScrollToPosition(finalScrollPosition);
            }
        });
    }

    public boolean isCanvasThemeVisible() {
        return layoutCanvasThemes.getVisibility() == View.VISIBLE;
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
        void onComponentTextViewAdded(ComponentTextView componentTextView);

        void onComponentImageViewAdded(ComponentImageView componentImageView);

        void onComponentBoxViewAdded(ComponentBoxView boxView);
    }


}
