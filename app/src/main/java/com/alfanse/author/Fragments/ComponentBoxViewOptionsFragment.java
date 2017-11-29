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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alfanse.author.CustomViews.ComponentBoxView;
import com.alfanse.author.CustomViews.QuoteCanvas;
import com.alfanse.author.R;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComponentBoxViewOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ComponentBoxViewOptionsFragment extends Fragment implements ColorPickerDialogListener {

    public static final int COMPONENT_BOXVIEW_OPTIONS_BG_COLOR_PICKER_DIALOG_ID = 42342;
    @BindView(R.id.layout_back_fragment_component_boxview_options)
    LinearLayout optionBack;
    @BindView(R.id.layout_bg_color_fragment_component_boxview_options)
    LinearLayout optionColorize;
    @BindView(R.id.layout_copy_fragment_component_boxview_options)
    LinearLayout optionCopy;
    @BindView(R.id.layout_bring_to_front_fragment_component_boxview_options)
    LinearLayout optionBringToFront;

    private Context mContext;
    private Activity mActivity;
    private QuoteCanvas mCanvas;
    private Unbinder unbinder;

    private OnFragmentInteractionListener mListener;
    private ComponentBoxView mComponentBoxView;

    public ComponentBoxViewOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_component_boxview_options, container, false);
        unbinder = ButterKnife.bind(this, view);
        initOptionItemClickListener();
        return view;
    }

    private void initOptionItemClickListener() {

        optionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentBackPressed();
                }
            }
        });


        optionColorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowPresets(true)
                        .setDialogId(COMPONENT_BOXVIEW_OPTIONS_BG_COLOR_PICKER_DIALOG_ID)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorBlack))
                        .setShowAlphaSlider(false)
                        .show(mActivity);
            }
        });

        optionCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ComponentBoxView componentBoxView = new ComponentBoxView(mContext, mCanvas);

                componentBoxView.setBackgroundColor(mComponentBoxView.getBackgroundColor());

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mCanvas.getWidth() / 2, mCanvas.getWidth() / 2);

                mCanvas.addView(componentBoxView, layoutParams);

                if (mListener != null) {
                    mListener.onComponentBoxViewAdded(componentBoxView);
                }
            }
        });

        optionBringToFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvas.bringChildToFront(mComponentBoxView);
            }
        });
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
        unbinder.unbind();
    }

    public void setQuoteCanvas(QuoteCanvas quoteCanvas) {
        mCanvas = quoteCanvas;
    }

    public void setComponentBoxView(ComponentBoxView componentBoxView) {

        mComponentBoxView = componentBoxView;
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        mComponentBoxView.setBackgroundColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

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
        void onFragmentBackPressed();

        void onComponentBoxViewAdded(ComponentBoxView componentBoxView);
    }
}
