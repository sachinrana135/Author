package com.alfanse.author.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.SquareFrameLayout;
import com.alfanse.author.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComponentTextViewOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComponentTextViewOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComponentTextViewOptionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.bottom_nav_fragment_component_textview_options)
    BottomNavigationView bottomNav;
    @BindView(R.id.layout_text_format_fragment_component_textview_options)
    LinearLayout layoutTextFormat;
    @BindView(R.id.layout_text_size_fragment_component_textview_options)
    LinearLayout layoutTextSize;
    @BindView(R.id.layout_font_family_fragment_component_textview_options)
    LinearLayout layoutFontFamily;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComponentTextViewOptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComponentTextViewOptionsFragment newInstance(String param1, String param2) {
        ComponentTextViewOptionsFragment fragment = new ComponentTextViewOptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_component_text_view_options, container, false);
        ButterKnife.bind(this, view);

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }
}
