package com.alfanse.author.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alfanse.author.Activities.WriteQuoteActivity;
import com.alfanse.author.CustomViews.ComponentImageView;
import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.SquareFrameLayout;
import com.alfanse.author.R;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WriteQuoteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WriteQuoteFragment extends Fragment implements
        CanvasOptionsFragment.OnFragmentInteractionListener,
        ComponentTextViewOptionsFragment.OnFragmentInteractionListener,
        ComponentImageViewOptionsFragment.OnFragmentInteractionListener {

    @BindView(R.id.canvas_write_quote_fragment) SquareFrameLayout mQuoteCanvas;

    private Context mContext;
    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private CanvasOptionsFragment mCanvasOptionsFragment;
    private ComponentImageViewOptionsFragment mComponentImageViewOptionsFragment;
    private ComponentTextViewOptionsFragment mComponentTextViewOptionsFragment;
    private android.support.v4.app.Fragment activeOptionFragment = null;

    private OnFragmentInteractionListener mListener;
    private ComponentTextView mActiveComponentTextView;
    private ComponentImageView mActiveComponentImageView;

    public WriteQuoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager = getChildFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_write_quote, container, false);
        ButterKnife.bind(this, view);
        mQuoteCanvas.setOnTouchListener(new CanvasTouchListener());
        if(activeOptionFragment == null) {
            activeOptionFragment = new CanvasOptionsFragment();
        }
        setCanvasBackground();
        return view;
    }

    private void setFragment() {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_write_quote_fragment, activeOptionFragment);
        transaction.commit();
    }

    private void loadCanvasOptionsFragment() {

        if(mCanvasOptionsFragment == null) {
            mCanvasOptionsFragment = new CanvasOptionsFragment();
            mCanvasOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_write_quote_fragment, mCanvasOptionsFragment);
        transaction.commit();
    }

    public void loadComponentImageViewOptionsFragment() {

        if (mComponentImageViewOptionsFragment == null) {
            mComponentImageViewOptionsFragment = new ComponentImageViewOptionsFragment();
            mComponentImageViewOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        }

        mComponentImageViewOptionsFragment.setComponentTextView(mActiveComponentImageView);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_write_quote_fragment, mComponentImageViewOptionsFragment);
        transaction.commit();
    }

    public void loadComponentTextViewOptionsFragment() {

        if(mComponentTextViewOptionsFragment == null) {
            mComponentTextViewOptionsFragment = new ComponentTextViewOptionsFragment();
            mCanvasOptionsFragment.setQuoteCanvas(mQuoteCanvas);
            mCanvasOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_write_quote_fragment, mComponentTextViewOptionsFragment);
        transaction.commit();
    }

    private void setCanvasBackground() {

        AssetManager assetManager = mContext.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open("image/background.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        mQuoteCanvas.setBackgroundImage(bitmap);
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


    @Override
    public void onAddComponentTextView(ComponentTextView componentTextView) {

        componentTextView.setOnTouchListener(new componentTextViewTouchListener());
        mActiveComponentTextView = componentTextView;
        loadComponentTextViewOptionsFragment();
    }

    @Override
    public void onAddComponentImageView(ComponentImageView componentImageView) {
        componentImageView.setOnTouchListener(new componentImageViewTouchListener());
        mActiveComponentImageView = componentImageView;
        loadComponentImageViewOptionsFragment();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

    private class CanvasTouchListener implements View.OnTouchListener  {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            loadCanvasOptionsFragment();
            return false;
        }
    }

    private class componentTextViewTouchListener implements View.OnTouchListener  {
        @Override
        public boolean onTouch(View componentTextView, MotionEvent event) {
            mActiveComponentTextView = (ComponentTextView) componentTextView;
            loadComponentTextViewOptionsFragment();
            return false;
        }
    }

    private class componentImageViewTouchListener implements View.OnTouchListener  {
        @Override
        public boolean onTouch(View componentImageView, MotionEvent event) {
            mActiveComponentImageView = (ComponentImageView) componentImageView;
            loadComponentImageViewOptionsFragment();
            return false;
        }
    }
}
