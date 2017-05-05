package com.alfanse.author.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.SquareFrameLayout;
import com.alfanse.author.R;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComponentTextViewOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ComponentTextViewOptionsFragment extends Fragment implements ColorPickerDialogListener {


    public static final int COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID = 101;

    @BindView(R.id.bottom_nav_fragment_component_textview_options)
    BottomNavigationView bottomNav;
    @BindView(R.id.layout_text_format_fragment_component_textview_options)
    LinearLayout layoutTextFormat;
    @BindView(R.id.layout_text_size_fragment_component_textview_options)
    LinearLayout layoutTextSize;
    @BindView(R.id.layout_font_family_fragment_component_textview_options)
    LinearLayout layoutFontFamily;

    private OnFragmentInteractionListener mListener;
    private SquareFrameLayout mCanvas;
    private ComponentTextView mComponentTextView;
    private Context mContext;
    private Activity mActivity;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

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

    public ComponentTextViewOptionsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_component_textview_options, container, false);
        ButterKnife.bind(this, view);

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentBackPressed();
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

    public void setQuoteCanvas(SquareFrameLayout quoteCanvas) {
        mCanvas = quoteCanvas;
    }

    public void setComponentTextView(ComponentTextView componentTextView) {
        mComponentTextView = componentTextView;
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {

        Toast.makeText(mActivity, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();

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
        void onFragmentBackPressed();
    }
}
