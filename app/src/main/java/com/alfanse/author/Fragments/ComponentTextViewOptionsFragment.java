package com.alfanse.author.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alfanse.author.Adapters.FontsAdapter;
import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.QuoteCanvas;
import com.alfanse.author.Models.Font;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.FontHelper;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComponentTextViewOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ComponentTextViewOptionsFragment extends Fragment implements ColorPickerDialogListener {


    public static final int COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID = 101;
    private final static String UNIT_TEXT_SIZE = "sp";
    @BindView(R.id.bottom_nav_fragment_component_textview_options)
    BottomNavigationView bottomNav;
    @BindView(R.id.layout_text_format_fragment_component_textview_options)
    LinearLayout layoutTextFormat;
    @BindView(R.id.layout_text_size_fragment_component_textview_options)
    LinearLayout layoutTextSize;
    @BindView(R.id.layout_font_family_fragment_component_textview_options)
    LinearLayout layoutFontFamily;
    @BindView(R.id.layout_format_left_align_fragment_component_textview_options)
    LinearLayout optionLeftAlign;
    @BindView(R.id.layout_format_center_align_fragment_component_textview_options)
    LinearLayout optionCenterAlign;
    @BindView(R.id.layout_format_right_align_fragment_component_textview_options)
    LinearLayout optionRightAlign;
    @BindView(R.id.layout_format_bold_fragment_component_textview_options)
    LinearLayout optionBold;
    @BindView(R.id.layout_format_italic_fragment_component_textview_options)
    LinearLayout optionItalic;
    @BindView(R.id.layout_format_underline_fragment_component_textview_options)
    LinearLayout optionUnderline;
    @BindView(R.id.layout_copy_fragment_component_textview_options)
    LinearLayout optionCopy;
    @BindView(R.id.layout_bring_to_front_fragment_component_textview_options)
    LinearLayout optionBringToFront;
    @BindView(R.id.layout_colorize_fragment_component_textview_options)
    LinearLayout optionColorize;
    @BindView(R.id.seekbar_textsize_fragment_component_textview_options)
    SeekBar optionSeekBarTextSize;
    @BindView(R.id.value_seekbar_fragment_component_textview_options)
    TextView optionSeekBarTextSizeValue;
    @BindView(R.id.rv_fonts_fragment_component_textview_options)
    RecyclerView recyclerViewFonts;
    private OnFragmentInteractionListener mListener;
    private QuoteCanvas mCanvas;
    private ComponentTextView mComponentTextView;
    private Context mContext;
    private Activity mActivity;
    private Unbinder unbinder;
    private FontsAdapter mFontsAdapter;
    private ArrayList<Font> mListFonts = new ArrayList<Font>();
    private LinearLayoutManager mLinearLayoutManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.bottom_nav_item_back_fragment_component_textview_options:
                    if (mListener != null) {
                        mListener.onFragmentBackPressed();
                    }
                    break;

                case R.id.bottom_nav_item_format_text_fragment_component_textview_options:

                    layoutTextSize.setVisibility(View.GONE);
                    layoutFontFamily.setVisibility(View.GONE);
                    layoutTextFormat.setVisibility(View.VISIBLE);

                    break;
                case R.id.bottom_nav_item_text_size_fragment_component_textview_options:

                    layoutTextSize.setVisibility(View.VISIBLE);
                    layoutFontFamily.setVisibility(View.GONE);
                    layoutTextFormat.setVisibility(View.GONE);

                    break;
                case R.id.bottom_nav_item_font_family_fragment_component_textview_options:

                    layoutTextSize.setVisibility(View.GONE);
                    layoutFontFamily.setVisibility(View.VISIBLE);
                    layoutTextFormat.setVisibility(View.GONE);

                    break;
            }
            return true;
        }

    };
    private SeekBar.OnSeekBarChangeListener textSizeSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            optionSeekBarTextSizeValue.setText(progress + " " + UNIT_TEXT_SIZE);
            mComponentTextView.setTextSize((float) progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    public ComponentTextViewOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListFonts = FontHelper.getInstance(mContext).getFontsArrayList();

        mFontsAdapter = new FontsAdapter(mContext, mListFonts, new FontsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Font font) {

                mComponentTextView.setTypeface(font.getFontTypeface());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_component_textview_options, container, false);
        unbinder = ButterKnife.bind(this, view);
        bottomNav.setSelectedItemId(R.id.bottom_nav_item_format_text_fragment_component_textview_options);
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        optionSeekBarTextSize.setOnSeekBarChangeListener(textSizeSeekBarListener);
        int mTextSize = Math.round(mComponentTextView.getTextSize());
        optionSeekBarTextSize.setProgress(mTextSize);
        optionSeekBarTextSizeValue.setText(mTextSize + " " + UNIT_TEXT_SIZE);

        mLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFonts.setLayoutManager(mLinearLayoutManager);
        recyclerViewFonts.setAdapter(mFontsAdapter);
        initOptionItemClickListener();
        return view;
    }

    private void initOptionItemClickListener() {

        optionLeftAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mComponentTextView.setAlignment(Gravity.LEFT);

            }
        });

        optionCenterAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComponentTextView.setAlignment(Gravity.CENTER_HORIZONTAL);
            }
        });

        optionRightAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComponentTextView.setAlignment(Gravity.RIGHT);
            }
        });

        optionBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComponentTextView.setTextBold();
            }
        });

        optionItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComponentTextView.setTextItalic();
            }
        });

        optionUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComponentTextView.setTextUnderline();
            }
        });

        optionCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ComponentTextView componentTextView = new ComponentTextView(mContext, mCanvas);

                componentTextView.copyTheme(mComponentTextView);

                componentTextView.setMinimumHeight(componentTextView.getMinimumHeight());

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (mCanvas.getWidth() * .7), FrameLayout.LayoutParams.WRAP_CONTENT);

                mCanvas.addView(componentTextView, layoutParams);

                if (mListener != null) {
                    mListener.onComponentTextViewAdded(componentTextView);
                }
            }
        });

        optionBringToFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvas.bringChildToFront(mComponentTextView);
            }
        });

        optionColorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowPresets(true)
                        .setDialogId(COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorBlack))
                        .setShowAlphaSlider(true)
                        .show(mActivity);
            }
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener = null;
        unbinder.unbind();
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

    public void setComponentTextView(ComponentTextView componentTextView) {
        mComponentTextView = componentTextView;
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        mComponentTextView.setTextColor(color);
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
        // TODO: Update argument type and name
        void onComponentTextViewAdded(ComponentTextView componentTextView);
        void onFragmentBackPressed();
    }
}
