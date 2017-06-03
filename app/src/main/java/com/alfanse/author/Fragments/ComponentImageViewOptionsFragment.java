package com.alfanse.author.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alfanse.author.CustomViews.ComponentImageView;
import com.alfanse.author.CustomViews.QuoteCanvas;
import com.alfanse.author.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComponentImageViewOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ComponentImageViewOptionsFragment extends Fragment {

    @BindView(R.id.layout_back_fragment_component_imageview_options)
    LinearLayout optionBack;
    @BindView(R.id.layout_copy_fragment_component_imageview_options)
    LinearLayout optionCopy;
    @BindView(R.id.layout_bring_to_front_fragment_component_imageview_options)
    LinearLayout optionBringToFront;

    private Context mContext;
    private Activity mActivity;
    private QuoteCanvas mCanvas;
    private Unbinder unbinder;

    private OnFragmentInteractionListener mListener;
    private ComponentImageView mComponentImageView;

    public ComponentImageViewOptionsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_component_imageview_options, container, false);
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

        optionCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ComponentImageView componentImageView = new ComponentImageView(mContext, mCanvas, null);

                componentImageView.setDrawable(mComponentImageView.getDrawable());

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                mCanvas.addView(componentImageView, layoutParams);

                if (mListener != null) {
                    mListener.onComponentImageViewAdded(componentImageView);
                }
            }
        });

        optionBringToFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvas.bringChildToFront(mComponentImageView);
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

    public void setComponentImageView(ComponentImageView componentImageView) {
        mComponentImageView = componentImageView;
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
        void onComponentImageViewAdded(ComponentImageView componentImageView);
        void onFragmentBackPressed();
    }
}
