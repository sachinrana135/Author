/*
 * Copyright (c) 2018. Alfanse Developers
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


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alfanse.author.Adapters.FilterAdapter;
import com.alfanse.author.CustomViews.QuoteCanvas;
import com.alfanse.author.Interfaces.onFilterItemClickListener;
import com.alfanse.author.Models.Filter;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.GPUImageFilterTools;
import com.alfanse.author.Utilities.SimpleSeekBarChangeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnhanceImageFragment extends BaseFragment {

    @BindView(R.id.layout_back_option_item)
    ViewGroup layoutBack;
    @BindView(R.id.layout_filter_option_item)
    ViewGroup layoutFiltersOption;
    @BindView(R.id.layout_bright_option_item)
    ViewGroup layoutBrightOption;
    @BindView(R.id.layout_contrast_option_item)
    ViewGroup layoutContrastOption;
    @BindView(R.id.layout_hue_option_item)
    ViewGroup layoutHueOption;
    @BindView(R.id.layout_saturation_option_item)
    ViewGroup layoutSaturationOption;
    @BindView(R.id.layout_rotate_option_item)
    ViewGroup layoutRotateOption;
    @BindView(R.id.layout_vignette_option_item)
    ViewGroup layoutVignetteOption;
    @BindView(R.id.layout_filters_list_fragment_enhance_image)
    ViewGroup layoutFilterList;
    @BindView(R.id.rv_filters_fragment_enhance_image)
    RecyclerView recyclerViewFilters;
    @BindView(R.id.layout_bright_fragment_enhance_image)
    ViewGroup layoutBrightness;
    @BindView(R.id.seekbar_bright_fragment_component_textview_options)
    SeekBar optionSeekBarBrightness;
    @BindView(R.id.bright_value_seekbar_fragment_component_textview_options)
    TextView optionSeekBarBrightnessValue;
    @BindView(R.id.layout_contrast_fragment_enhance_image)
    ViewGroup layoutContrast;
    @BindView(R.id.seekbar_contrast_fragment_component_textview_options)
    SeekBar optionSeekBarContrast;
    @BindView(R.id.contrast_value_seekbar_fragment_component_textview_options)
    TextView optionSeekBarContrastValue;
    @BindView(R.id.layout_satur_fragment_enhance_image)
    ViewGroup layoutSatur;
    @BindView(R.id.seekbar_satur_fragment_component_textview_options)
    SeekBar optionSeekBarSatur;
    @BindView(R.id.satur_value_seekbar_fragment_component_textview_options)
    TextView optionSeekBarSaturValue;
    @BindView(R.id.layout_hue_fragment_enhance_image)
    ViewGroup layoutHue;
    @BindView(R.id.seekbar_hue_fragment_component_textview_options)
    SeekBar optionSeekBarHue;
    @BindView(R.id.hue_value_seekbar_fragment_component_textview_options)
    TextView optionSeekBarHueValue;
    @BindView(R.id.layout_rotate_fragment_enhance_image)
    ViewGroup layoutRotate;
    @BindView(R.id.seekbar_rotate_fragment_component_textview_options)
    SeekBar optionSeekBarRotate;
    @BindView(R.id.rotate_value_seekbar_fragment_component_textview_options)
    TextView optionSeekBarRotateValue;
    @BindView(R.id.layout_vignette_fragment_enhance_image)
    ViewGroup layoutVignette;
    @BindView(R.id.seekbar_filter_adjuster)
    SeekBar filterAdjuster;
    @BindView(R.id.seekbar_vignette_fragment_component_textview_options)
    SeekBar optionSeekBarVignette;
    @BindView(R.id.text_enhance_type)
    TextView textEnhanceType;

    private LinearLayoutManager mFilterLinearLayoutManager;
    private QuoteCanvas mCanvas;
    private List<ViewGroup> optionsLayout;
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private Activity mActivity;
    private FilterAdapter mFiltersAdapter;
    private ArrayList<Filter> mListFilters = new ArrayList<Filter>();
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    private int defaultBrightnessLevel = 50;
    private int defaultContrastLevel = 50;
    private int defaultHueLevel = 0;
    private int defaultSaturLevel = 50;
    private int defaultVignetteLevel = 75;
    private int defaultRotateLevel = 0;

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SimpleSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            adjustFilter(progress);
        }
    };

    public EnhanceImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        mListFilters = new ArrayList<Filter>();
        mFiltersAdapter = new FilterAdapter(mContext, mListFilters, mOnFilterItemClickListener);

        loadFilters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enhance_image, container, false);
        ButterKnife.bind(this, view);
        optionsLayout = Arrays.asList(layoutFilterList, layoutBrightness, layoutContrast, layoutSatur, layoutHue, layoutRotate, layoutVignette);
        mFilterLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFilters.setLayoutManager(mFilterLinearLayoutManager);
        recyclerViewFilters.setAdapter(mFiltersAdapter);

        optionSeekBarBrightness.setOnSeekBarChangeListener(seekBarListener);

        optionSeekBarContrast.setOnSeekBarChangeListener(seekBarListener);

        optionSeekBarHue.setOnSeekBarChangeListener(seekBarListener);

        optionSeekBarSatur.setOnSeekBarChangeListener(seekBarListener);

        optionSeekBarVignette.setMax(75);
        optionSeekBarVignette.setOnSeekBarChangeListener(seekBarListener);

        optionSeekBarRotate.setOnSeekBarChangeListener(seekBarListener);

        filterAdjuster.setOnSeekBarChangeListener(seekBarListener);

        return view;
    }

    public void setQuoteCanvas(QuoteCanvas quoteCanvas) {
        mCanvas = quoteCanvas;
    }

    @OnClick(R.id.layout_back_option_item)
    void onBackLayoutClick() {
        mListener.onFragmentBackPressed();
    }

    @OnClick(R.id.layout_filter_option_item)
    void onFilterListLayoutClick() {
        Filter filter = getActiveFilter();
        GPUImageFilter gpuImageFilter = mCanvas.getFilterByType(filter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
        showLayout(layoutFilterList);
        textEnhanceType.setVisibility(View.GONE);
    }

    @OnClick(R.id.layout_bright_option_item)
    void onBrightLayoutClick() {
        Filter filter = new Filter(getString(R.string.option_item_bright), GPUImageFilterTools.FilterType.BRIGHTNESS, false);
        boolean isFilterApplied = mCanvas.isFilterApplied(filter);
        if (!isFilterApplied) {
            mCanvas.setFilter(filter);
        }
        GPUImageFilter gpuImageFilter = mCanvas.getFilterByType(filter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
        optionSeekBarBrightness.setProgress(isFilterApplied ? optionSeekBarBrightness.getProgress() : defaultBrightnessLevel);
        showLayout(layoutBrightness);
        textEnhanceType.setVisibility(View.VISIBLE);
        textEnhanceType.setText(getString(R.string.option_item_bright));
    }

    @OnClick(R.id.layout_contrast_option_item)
    void onContrastLayoutClick() {
        Filter filter = new Filter(getString(R.string.option_item_contrast), GPUImageFilterTools.FilterType.CONTRAST, false);
        boolean isFilterApplied = mCanvas.isFilterApplied(filter);
        if (!isFilterApplied) {
            mCanvas.setFilter(filter);
        }
        GPUImageFilter gpuImageFilter = mCanvas.getFilterByType(filter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
        optionSeekBarContrast.setProgress(optionSeekBarContrast.getProgress() == 0 ? defaultContrastLevel : optionSeekBarContrast.getProgress());
        optionSeekBarContrast.setProgress(isFilterApplied ? optionSeekBarContrast.getProgress() : defaultContrastLevel);
        showLayout(layoutContrast);
        textEnhanceType.setVisibility(View.VISIBLE);
        textEnhanceType.setText(getString(R.string.option_item_contrast));
    }

    @OnClick(R.id.layout_hue_option_item)
    void onHueLayoutClick() {
        Filter filter = new Filter(getString(R.string.option_item_hue), GPUImageFilterTools.FilterType.HUE, false);
        boolean isFilterApplied = mCanvas.isFilterApplied(filter);
        if (!isFilterApplied) {
            mCanvas.setFilter(filter);
        }
        GPUImageFilter gpuImageFilter = mCanvas.getFilterByType(filter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
        optionSeekBarHue.setProgress(isFilterApplied ? optionSeekBarHue.getProgress() : defaultHueLevel);
        showLayout(layoutHue);
        textEnhanceType.setVisibility(View.VISIBLE);
        textEnhanceType.setText(getString(R.string.option_item_hue));
    }

    @OnClick(R.id.layout_saturation_option_item)
    void onSaturationLayoutClick() {
        Filter filter = new Filter(getString(R.string.option_item_satur), GPUImageFilterTools.FilterType.SATURATION, false);
        boolean isFilterApplied = mCanvas.isFilterApplied(filter);
        if (!isFilterApplied) {
            mCanvas.setFilter(filter);
        }
        GPUImageFilter gpuImageFilter = mCanvas.getFilterByType(filter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
        optionSeekBarSatur.setProgress(isFilterApplied ? optionSeekBarSatur.getProgress() : defaultSaturLevel);
        showLayout(layoutSatur);
        textEnhanceType.setVisibility(View.VISIBLE);
        textEnhanceType.setText(getString(R.string.option_item_satur));
    }

    @OnClick(R.id.layout_vignette_option_item)
    void onVignetteLayoutClick() {
        Filter filter = new Filter(getString(R.string.option_item_vignette), GPUImageFilterTools.FilterType.VIGNETTE, false);
        boolean isFilterApplied = mCanvas.isFilterApplied(filter);
        if (!isFilterApplied) {
            mCanvas.setFilter(filter);
        }
        GPUImageFilter gpuImageFilter = mCanvas.getFilterByType(filter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
        optionSeekBarVignette.setProgress(isFilterApplied ? optionSeekBarVignette.getProgress() : defaultVignetteLevel);
        showLayout(layoutVignette);
        textEnhanceType.setVisibility(View.VISIBLE);
        textEnhanceType.setText(getString(R.string.option_item_vignette));
    }

    @OnClick(R.id.layout_rotate_option_item)
    void onRotateLayoutClick() {
        Filter filter = new Filter(getString(R.string.text_rotate), GPUImageFilterTools.FilterType.TRANSFORM2D, false);
        boolean isFilterApplied = mCanvas.isFilterApplied(filter);
        if (!isFilterApplied) {
            mCanvas.setFilter(filter);
        }
        GPUImageFilter gpuImageFilter = mCanvas.getFilterByType(filter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
        optionSeekBarRotate.setProgress(isFilterApplied ? optionSeekBarRotate.getProgress() : defaultRotateLevel);
        showLayout(layoutRotate);
        textEnhanceType.setVisibility(View.VISIBLE);
        textEnhanceType.setText(getString(R.string.text_rotate));
    }

    void showLayout(ViewGroup view) {
        for (ViewGroup v : optionsLayout) {
            v.setVisibility(v == view ? View.VISIBLE : View.GONE);
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

    public interface OnFragmentInteractionListener {
        void onFragmentBackPressed();
    }

    private void loadFilters() {

        mListFilters.clear();

        Filter originalFilter = new Filter(getString(R.string.original), null, true);
        mListFilters.add(originalFilter);
        mListFilters.add(new Filter("Invert", GPUImageFilterTools.FilterType.INVERT, false));
        mListFilters.add(new Filter("Pixelation", GPUImageFilterTools.FilterType.PIXELATION, false));
        mListFilters.add(new Filter("Gamma", GPUImageFilterTools.FilterType.GAMMA, false));
        mListFilters.add(new Filter("Sepia", GPUImageFilterTools.FilterType.SEPIA, false));
        mListFilters.add(new Filter("Grayscale", GPUImageFilterTools.FilterType.GRAYSCALE, false));
        mListFilters.add(new Filter("Sharpness", GPUImageFilterTools.FilterType.SHARPEN, false));
        mListFilters.add(new Filter("Sobel Edge Detection", GPUImageFilterTools.FilterType.SOBEL_EDGE_DETECTION, false));
        mListFilters.add(new Filter("3x3 Convolution", GPUImageFilterTools.FilterType.THREE_X_THREE_CONVOLUTION, false));
        mListFilters.add(new Filter("Emboss", GPUImageFilterTools.FilterType.EMBOSS, false));
        mListFilters.add(new Filter("Posterize", GPUImageFilterTools.FilterType.POSTERIZE, false));
        mListFilters.add(new Filter("Grouped filters", GPUImageFilterTools.FilterType.FILTER_GROUP, false));
        mListFilters.add(new Filter("Exposure", GPUImageFilterTools.FilterType.EXPOSURE, false));
        mListFilters.add(new Filter("Highlight Shadow", GPUImageFilterTools.FilterType.HIGHLIGHT_SHADOW, false));
        mListFilters.add(new Filter("Monochrome", GPUImageFilterTools.FilterType.MONOCHROME, false));
        mListFilters.add(new Filter("Opacity", GPUImageFilterTools.FilterType.OPACITY, false));
        mListFilters.add(new Filter("RGB", GPUImageFilterTools.FilterType.RGB, false));
        mListFilters.add(new Filter("White Balance", GPUImageFilterTools.FilterType.WHITE_BALANCE, false));
        mListFilters.add(new Filter("ToneCurve", GPUImageFilterTools.FilterType.TONE_CURVE, false));
        mListFilters.add(new Filter("Lookup (Amatorka, false))", GPUImageFilterTools.FilterType.LOOKUP_AMATORKA, false));
        mListFilters.add(new Filter("Gaussian Blur", GPUImageFilterTools.FilterType.GAUSSIAN_BLUR, false));
        mListFilters.add(new Filter("Crosshatch", GPUImageFilterTools.FilterType.CROSSHATCH, false));
        mListFilters.add(new Filter("Box Blur", GPUImageFilterTools.FilterType.BOX_BLUR, false));
        mListFilters.add(new Filter("CGA Color Space", GPUImageFilterTools.FilterType.CGA_COLORSPACE, false));
        mListFilters.add(new Filter("Dilation", GPUImageFilterTools.FilterType.DILATION, false));
        mListFilters.add(new Filter("Kuwahara", GPUImageFilterTools.FilterType.KUWAHARA, false));
        mListFilters.add(new Filter("RGB Dilation", GPUImageFilterTools.FilterType.RGB_DILATION, false));
        mListFilters.add(new Filter("Sketch", GPUImageFilterTools.FilterType.SKETCH, false));
        mListFilters.add(new Filter("Toon", GPUImageFilterTools.FilterType.TOON, false));
        mListFilters.add(new Filter("Smooth Toon", GPUImageFilterTools.FilterType.SMOOTH_TOON, false));
        mListFilters.add(new Filter("Halftone", GPUImageFilterTools.FilterType.HALFTONE, false));
        mListFilters.add(new Filter("Bulge Distortion", GPUImageFilterTools.FilterType.BULGE_DISTORTION, false));
        mListFilters.add(new Filter("Glass Sphere", GPUImageFilterTools.FilterType.GLASS_SPHERE, false));
        mListFilters.add(new Filter("Haze", GPUImageFilterTools.FilterType.HAZE, false));
        mListFilters.add(new Filter("Laplacian", GPUImageFilterTools.FilterType.LAPLACIAN, false));
        mListFilters.add(new Filter("Non Maximum Suppression", GPUImageFilterTools.FilterType.NON_MAXIMUM_SUPPRESSION, false));
        mListFilters.add(new Filter("Sphere Refraction", GPUImageFilterTools.FilterType.SPHERE_REFRACTION, false));
        mListFilters.add(new Filter("Swirl", GPUImageFilterTools.FilterType.SWIRL, false));
        mListFilters.add(new Filter("Weak Pixel Inclusion", GPUImageFilterTools.FilterType.WEAK_PIXEL_INCLUSION, false));
        mListFilters.add(new Filter("False Color", GPUImageFilterTools.FilterType.FALSE_COLOR, false));
        mListFilters.add(new Filter("Color Balance", GPUImageFilterTools.FilterType.COLOR_BALANCE, false));
        mListFilters.add(new Filter("Levels Min (Mid Adjust, false))", GPUImageFilterTools.FilterType.LEVELS_FILTER_MIN, false));
        mListFilters.add(new Filter("Bilateral Blur", GPUImageFilterTools.FilterType.BILATERAL_BLUR, false));

        setActiveFilter(originalFilter);
        mCanvas.setFilter(originalFilter);

        mFiltersAdapter.notifyDataSetChanged();

    }

    private void setActiveFilter(Filter filter) {
        for (Filter f : mListFilters) {
            f.setSelected(false);
        }
        mListFilters.get(mListFilters.indexOf(filter)).setSelected(true);
        mFiltersAdapter.notifyDataSetChanged();
    }

    private Filter getActiveFilter() {
        Filter filter = null;
        for (Filter f : mListFilters) {
            if (f.isSelected() == true) {
                filter = f;
                break;
            }
        }
        return filter;
    }

    private onFilterItemClickListener mOnFilterItemClickListener = new onFilterItemClickListener() {
        @Override
        public void onItemClick(Filter filter) {
            textEnhanceType.setVisibility(View.GONE);
            setActiveFilter(filter);
            mCanvas.setFilter(filter);
            GPUImageFilter gpuImageFilter = mCanvas.getFilterByType(filter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
            filterAdjuster.setVisibility(mFilterAdjuster.canAdjust() ? View.VISIBLE : View.INVISIBLE);
        }
    };

    public void adjustFilter(int progress) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
        mCanvas.requestRender();
    }
}
