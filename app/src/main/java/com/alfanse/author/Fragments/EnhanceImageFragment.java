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
import com.alfanse.author.Interfaces.XmlClickable;
import com.alfanse.author.Interfaces.onFilterItemClickListener;
import com.alfanse.author.Models.Filter;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.GPUImageFilterTools;

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
public class EnhanceImageFragment extends BaseFragment implements XmlClickable {

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
    @BindView(R.id.layout_flip_option_item)
    ViewGroup layoutRotateOption;
    @BindView(R.id.layout_tint_option_item)
    ViewGroup layoutTintOption;
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
    @BindView(R.id.layout_flip_fragment_enhance_image)
    ViewGroup layoutRotate;
    @BindView(R.id.seekbar_rotate_fragment_component_textview_options)
    SeekBar optionSeekBarRotate;
    @BindView(R.id.rotate_value_seekbar_fragment_component_textview_options)
    TextView optionSeekBarRotateValue;
    @BindView(R.id.layout_tint_fragment_enhance_image)
    ViewGroup layoutTint;
    @BindView(R.id.seekbar_filter_adjuster)
    SeekBar filterAdjuster;

    private LinearLayoutManager mFilterLinearLayoutManager;
    private QuoteCanvas mCanvas;
    private List<ViewGroup> optionsLayout;
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private Activity mActivity;
    private FilterAdapter mFiltersAdapter;
    private ArrayList<Filter> mListFilters = new ArrayList<Filter>();
    private boolean isApplyingFilter = false;
    private int tint_color = 0xFF1E8D24;
    private boolean flipVertical = false;
    private boolean flipHorizontal = false;
    private int rotationAngle;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    private SeekBar.OnSeekBarChangeListener brightnessSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            optionSeekBarBrightnessValue.setText(Integer.toString(progress - 100) + "%");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            optionSeekBarBrightness.setEnabled(false);
            isApplyingFilter = true;

//            mCanvas.applyBrightnessFilter(seekBar.getProgress() - 100, new bitmapFilterListener() {
//                @Override
//                public void onSuccuess() {
//                    optionSeekBarBrightness.setEnabled(true);
//                    isApplyingFilter = false;
//                }
//
//                @Override
//                public void onError() {
//                    optionSeekBarBrightness.setEnabled(true);
//                    isApplyingFilter = false;
//                }
//            });
        }
    };

    private SeekBar.OnSeekBarChangeListener contrastSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            optionSeekBarContrastValue.setText(Integer.toString(progress - 100) + "%");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            optionSeekBarContrast.setEnabled(false);
            isApplyingFilter = true;

//            mCanvas.applyContrastFilter(seekBar.getProgress() - 100, new bitmapFilterListener() {
//                @Override
//                public void onSuccuess() {
//                    optionSeekBarContrast.setEnabled(true);
//                    isApplyingFilter = false;
//                }
//
//                @Override
//                public void onError() {
//                    optionSeekBarContrast.setEnabled(true);
//                    isApplyingFilter = false;
//                }
//            });
        }
    };

    private SeekBar.OnSeekBarChangeListener saturSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            optionSeekBarSaturValue.setText(Integer.toString(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            optionSeekBarSatur.setEnabled(false);
            isApplyingFilter = true;

//            mCanvas.applySaturationFilter(seekBar.getProgress(), new bitmapFilterListener() {
//                @Override
//                public void onSuccuess() {
//                    optionSeekBarSatur.setEnabled(true);
//                    isApplyingFilter = false;
//                }
//
//                @Override
//                public void onError() {
//                    optionSeekBarSatur.setEnabled(true);
//                    isApplyingFilter = false;
//                }
//            });
        }
    };

    private SeekBar.OnSeekBarChangeListener hueSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            optionSeekBarHueValue.setText(Integer.toString(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            optionSeekBarHue.setEnabled(false);
            isApplyingFilter = true;
//            mCanvas.applyHueFilter(seekBar.getProgress(), new bitmapFilterListener() {
//                @Override
//                public void onSuccuess() {
//                    optionSeekBarHue.setEnabled(true);
//                    isApplyingFilter = false;
//                }
//
//                @Override
//                public void onError() {
//                    optionSeekBarHue.setEnabled(true);
//                    isApplyingFilter = false;
//                }
//            });
        }
    };

    private SeekBar.OnSeekBarChangeListener rotateSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            rotationAngle = progress;
            optionSeekBarRotateValue.setText(Integer.toString(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            flipCanvas();
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
        optionsLayout = Arrays.asList(layoutFilterList, layoutBrightness, layoutContrast, layoutSatur, layoutHue, layoutRotate, layoutTint);
        mFilterLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFilters.setLayoutManager(mFilterLinearLayoutManager);
        recyclerViewFilters.setAdapter(mFiltersAdapter);

        optionSeekBarBrightness.setOnSeekBarChangeListener(brightnessSeekBarListener);
        optionSeekBarContrast.setOnSeekBarChangeListener(contrastSeekBarListener);
        optionSeekBarSatur.setOnSeekBarChangeListener(saturSeekBarListener);
        optionSeekBarHue.setOnSeekBarChangeListener(hueSeekBarListener);
        optionSeekBarRotate.setOnSeekBarChangeListener(rotateSeekBarListener);

//        optionSeekBarHue.setProgress(mCanvas.getHueLevel());
//        optionSeekBarHueValue.setText(Integer.toString(mCanvas.getHueLevel()));
//
//        optionSeekBarBrightness.setProgress(mCanvas.getBrightnessLevel() + 100);
//        optionSeekBarBrightnessValue.setText(Integer.toString(mCanvas.getBrightnessLevel()) + "%");
//
//        optionSeekBarContrast.setProgress(mCanvas.getContrastLevel() + 100);
//        optionSeekBarContrastValue.setText(Integer.toString(mCanvas.getContrastLevel()) + "%");
//
//        optionSeekBarSatur.setProgress(mCanvas.getSaturationLevel());
//        optionSeekBarSaturValue.setText(Integer.toString(mCanvas.getSaturationLevel()));
//
//        optionSeekBarRotate.setProgress(mCanvas.getRotationAngle());
//        optionSeekBarSaturValue.setText(Integer.toString(mCanvas.getRotationAngle()));

        return view;
    }

    public void setQuoteCanvas(QuoteCanvas quoteCanvas) {
        mCanvas = quoteCanvas;
    }

    @OnClick(R.id.layout_back_option_item)
    void onBackLayoutClick() {
        if (!isApplyingFilter) {
            mListener.onFragmentBackPressed();
        }
    }

    @OnClick(R.id.layout_filter_option_item)
    void onFilterListLayoutClick() {
        showLayout(layoutFilterList);
    }

    @OnClick(R.id.layout_bright_option_item)
    void onBrightLayoutClick() {
        showLayout(layoutBrightness);
    }

    @OnClick(R.id.layout_contrast_option_item)
    void onContrastLayoutClick() {
        showLayout(layoutContrast);
    }

    @OnClick(R.id.layout_hue_option_item)
    void onHueLayoutClick() {
        showLayout(layoutHue);
    }

    @OnClick(R.id.layout_saturation_option_item)
    void onSaturationLayoutClick() {
        showLayout(layoutSatur);
    }

    @OnClick(R.id.layout_flip_option_item)
    void onRotateLayoutClick() {
        showLayout(layoutRotate);
    }

    @OnClick(R.id.layout_tint_option_item)
    void onTintLayoutClick() {
        showLayout(layoutTint);
    }

    @OnClick(R.id.flip_v)
    void onVerticalFlipClick() {
        flipVertical = !flipVertical;
        flipCanvas();
    }

    @OnClick(R.id.flip_h)
    void onHorizonFlipClick() {
        flipHorizontal = !flipHorizontal;
        flipCanvas();
    }

    private void flipCanvas() {

        optionSeekBarRotate.setEnabled(false);
        isApplyingFilter = true;

//        mCanvas.applyRotateFilter(rotationAngle, flipHorizontal, flipVertical, new bitmapFilterListener() {
//            @Override
//            public void onSuccuess() {
//                optionSeekBarRotate.setEnabled(true);
//                isApplyingFilter = false;
//            }
//
//            @Override
//            public void onError() {
//                optionSeekBarRotate.setEnabled(true);
//                isApplyingFilter = false;
//            }
//        });
    }

    @OnClick(R.id.img_cancel_hue)
    void onCancelHueClick() {
        //mCanvas.resetOriginalBitmap();
        optionSeekBarHue.setProgress(0);
        optionSeekBarHueValue.setText("0");
    }

    void showLayout(ViewGroup view) {

        for (ViewGroup v : optionsLayout) {
            if (v == view) {
                v.setVisibility(View.VISIBLE);
                if (view == layoutFilterList) {
//                    int scrollPosition = mCanvas.getFilter() != null ? mListFilters.indexOf(mCanvas.getFilter()) : 0;
//                    if (scrollPosition < (mListFilters.size() - 1)) {
//                        scrollPosition = scrollPosition + 1;
//                    }
//                    final int finalScrollPosition = scrollPosition;
//                    new Handler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            recyclerViewFilters.smoothScrollToPosition(finalScrollPosition);
//                        }
//                    });
                }
            } else {
                v.setVisibility(View.GONE);
            }
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

    @Override
    public void tintItemClick(View view) {
//        if (view.getTag().toString().equalsIgnoreCase(getString(R.string.text_original))) {
//            mCanvas.resetOriginalBitmap();
//        } else {
//            if (tint_color != Color.parseColor(view.getTag().toString())) {
//                isApplyingFilter = true;
//                tint_color = Color.parseColor(view.getTag().toString());
//                mCanvas.applyTint(tint_color, new bitmapFilterListener() {
//                    @Override
//                    public void onSuccuess() {
//                        isApplyingFilter = false;
//                    }
//
//                    @Override
//                    public void onError() {
//                        isApplyingFilter = false;
//                    }
//                });
//            }
//        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentBackPressed();
    }

    private void loadFilters() {

        mListFilters.clear();

        Filter originalFilter = new Filter(getString(R.string.original), null, true);
        mListFilters.add(originalFilter);
        mListFilters.add(new Filter("Contrast", GPUImageFilterTools.FilterType.CONTRAST, false));
        mListFilters.add(new Filter("Invert", GPUImageFilterTools.FilterType.INVERT, false));
        mListFilters.add(new Filter("Pixelation", GPUImageFilterTools.FilterType.PIXELATION, false));
        mListFilters.add(new Filter("Hue", GPUImageFilterTools.FilterType.HUE, false));
        mListFilters.add(new Filter("Gamma", GPUImageFilterTools.FilterType.GAMMA, false));
        mListFilters.add(new Filter("Brightness", GPUImageFilterTools.FilterType.BRIGHTNESS, false));
        mListFilters.add(new Filter("Sepia", GPUImageFilterTools.FilterType.SEPIA, false));
        mListFilters.add(new Filter("Grayscale", GPUImageFilterTools.FilterType.GRAYSCALE, false));
        mListFilters.add(new Filter("Sharpness", GPUImageFilterTools.FilterType.SHARPEN, false));
        mListFilters.add(new Filter("Sobel Edge Detection", GPUImageFilterTools.FilterType.SOBEL_EDGE_DETECTION, false));
        mListFilters.add(new Filter("3x3 Convolution", GPUImageFilterTools.FilterType.THREE_X_THREE_CONVOLUTION, false));
        mListFilters.add(new Filter("Emboss", GPUImageFilterTools.FilterType.EMBOSS, false));
        mListFilters.add(new Filter("Posterize", GPUImageFilterTools.FilterType.POSTERIZE, false));
        mListFilters.add(new Filter("Grouped filters", GPUImageFilterTools.FilterType.FILTER_GROUP, false));
        mListFilters.add(new Filter("Saturation", GPUImageFilterTools.FilterType.SATURATION, false));
        mListFilters.add(new Filter("Exposure", GPUImageFilterTools.FilterType.EXPOSURE, false));
        mListFilters.add(new Filter("Highlight Shadow", GPUImageFilterTools.FilterType.HIGHLIGHT_SHADOW, false));
        mListFilters.add(new Filter("Monochrome", GPUImageFilterTools.FilterType.MONOCHROME, false));
        mListFilters.add(new Filter("Opacity", GPUImageFilterTools.FilterType.OPACITY, false));
        mListFilters.add(new Filter("RGB", GPUImageFilterTools.FilterType.RGB, false));
        mListFilters.add(new Filter("White Balance", GPUImageFilterTools.FilterType.WHITE_BALANCE, false));
        mListFilters.add(new Filter("Vignette", GPUImageFilterTools.FilterType.VIGNETTE, false));
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
        mListFilters.add(new Filter("Transform (2-D, false))", GPUImageFilterTools.FilterType.TRANSFORM2D, false));

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

    private onFilterItemClickListener mOnFilterItemClickListener = new onFilterItemClickListener() {

        @Override
        public void onItemClick(Filter filter) {
            setActiveFilter(filter);
            mCanvas.setFilter(filter);
            GPUImageFilter gpuImageFilter = mCanvas.getFilterByType(filter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageFilter);
            filterAdjuster.setVisibility(mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*
        * Below code is to detect the back press event in fragment
        * *
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return isApplyingFilter ? true : false;
                    }
                }
                return false;
            }
        });*/
    }
}
