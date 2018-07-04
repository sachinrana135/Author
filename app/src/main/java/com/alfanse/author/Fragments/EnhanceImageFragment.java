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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.alfanse.author.Interfaces.bitmapFilterListener;
import com.alfanse.author.Interfaces.onFilterItemClickListener;
import com.alfanse.author.Models.Filter;
import com.alfanse.author.R;

import net.alhazmy13.imagefilter.ImageFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.layout_temp_option_item)
    ViewGroup layoutTempOption;
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
    @BindView(R.id.layout_temp_fragment_enhance_image)
    ViewGroup layoutTemp;
    @BindView(R.id.seekbar_temp_fragment_component_textview_options)
    SeekBar optionSeekBarTemp;
    @BindView(R.id.temp_value_seekbar_fragment_component_textview_options)
    TextView optionSeekBarTempValue;
    @BindView(R.id.layout_tint_fragment_enhance_image)
    ViewGroup layoutTint;

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

            mCanvas.applyBrightnessFilter(seekBar.getProgress() - 100, new bitmapFilterListener() {
                @Override
                public void onSuccuess() {
                    optionSeekBarBrightness.setEnabled(true);
                    isApplyingFilter = false;
                }

                @Override
                public void onError() {
                    optionSeekBarBrightness.setEnabled(true);
                    isApplyingFilter = false;
                }
            });
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

            mCanvas.applyContrastFilter(seekBar.getProgress() - 100, new bitmapFilterListener() {
                @Override
                public void onSuccuess() {
                    optionSeekBarContrast.setEnabled(true);
                    isApplyingFilter = false;
                }

                @Override
                public void onError() {
                    optionSeekBarContrast.setEnabled(true);
                    isApplyingFilter = false;
                }
            });
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

            mCanvas.applySaturationFilter(seekBar.getProgress(), new bitmapFilterListener() {
                @Override
                public void onSuccuess() {
                    optionSeekBarSatur.setEnabled(true);
                    isApplyingFilter = false;
                }

                @Override
                public void onError() {
                    optionSeekBarSatur.setEnabled(true);
                    isApplyingFilter = false;
                }
            });
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
            mCanvas.applyHueFilter(seekBar.getProgress(), new bitmapFilterListener() {
                @Override
                public void onSuccuess() {
                    optionSeekBarHue.setEnabled(true);
                    isApplyingFilter = false;
                }

                @Override
                public void onError() {
                    optionSeekBarHue.setEnabled(true);
                    isApplyingFilter = false;
                }
            });
        }
    };

    private SeekBar.OnSeekBarChangeListener tempSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            optionSeekBarTempValue.setText(Integer.toString(seekBar.getProgress()));
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
        optionsLayout = Arrays.asList(layoutFilterList, layoutBrightness, layoutContrast, layoutSatur, layoutHue, layoutTemp, layoutTint);
        mFilterLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFilters.setLayoutManager(mFilterLinearLayoutManager);
        recyclerViewFilters.setAdapter(mFiltersAdapter);

        optionSeekBarBrightness.setOnSeekBarChangeListener(brightnessSeekBarListener);
        optionSeekBarContrast.setOnSeekBarChangeListener(contrastSeekBarListener);
        optionSeekBarSatur.setOnSeekBarChangeListener(saturSeekBarListener);
        optionSeekBarHue.setOnSeekBarChangeListener(hueSeekBarListener);
        optionSeekBarTemp.setOnSeekBarChangeListener(tempSeekBarListener);

        optionSeekBarHue.setProgress(mCanvas.getHueLevel());
        optionSeekBarHueValue.setText(Integer.toString(mCanvas.getHueLevel()));

        optionSeekBarBrightness.setProgress(mCanvas.getBrightnessLevel() + 100);
        optionSeekBarBrightnessValue.setText(Integer.toString(mCanvas.getBrightnessLevel()) + "%");

        optionSeekBarContrast.setProgress(mCanvas.getContrastLevel() + 100);
        optionSeekBarContrastValue.setText(Integer.toString(mCanvas.getContrastLevel()) + "%");

        optionSeekBarSatur.setProgress(mCanvas.getSaturationLevel());
        optionSeekBarSaturValue.setText(Integer.toString(mCanvas.getSaturationLevel()));

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

    @OnClick(R.id.layout_temp_option_item)
    void onTempLayoutClick() {
        showLayout(layoutTemp);
    }

    @OnClick(R.id.layout_tint_option_item)
    void onTintLayoutClick() {
        showLayout(layoutTint);
    }

    @OnClick(R.id.img_cancel_hue)
    void onCancelHueClick() {
        mCanvas.resetOriginalBitmap();
        optionSeekBarHue.setProgress(0);
        optionSeekBarHueValue.setText("0");
    }

    void showLayout(ViewGroup view) {

        for (ViewGroup v : optionsLayout) {
            if (v == view) {
                v.setVisibility(View.VISIBLE);
                if (view == layoutFilterList) {
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
        if (view.getTag().toString().equalsIgnoreCase(getString(R.string.text_original))) {
            mCanvas.resetOriginalBitmap();
        } else {
            if (tint_color != Color.parseColor(view.getTag().toString())) {
                isApplyingFilter = true;
                tint_color = Color.parseColor(view.getTag().toString());
                mCanvas.applyTint(tint_color, new bitmapFilterListener() {
                    @Override
                    public void onSuccuess() {
                        isApplyingFilter = false;
                    }

                    @Override
                    public void onError() {
                        isApplyingFilter = false;
                    }
                });
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentBackPressed();
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

    private onFilterItemClickListener mOnFilterItemClickListener = new onFilterItemClickListener() {

        @Override
        public void onItemClick(Filter filter) {
            setActiveFilter(filter);
            mCanvas.setFilter(filter);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*
        * Below code is to detect the back press event in fragme]p09
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
