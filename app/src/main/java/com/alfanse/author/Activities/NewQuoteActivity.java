package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.alfanse.author.CustomViews.ComponentImageView;
import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.SquareFrameLayout;
import com.alfanse.author.Fragments.CanvasOptionsFragment;
import com.alfanse.author.Fragments.ComponentImageViewOptionsFragment;
import com.alfanse.author.Fragments.ComponentTextViewOptionsFragment;
import com.alfanse.author.R;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewQuoteActivity extends AppCompatActivity implements
        CanvasOptionsFragment.OnFragmentInteractionListener,
        ComponentTextViewOptionsFragment.OnFragmentInteractionListener,
        ComponentImageViewOptionsFragment.OnFragmentInteractionListener {

    @BindView(R.id.SquareFrameLayoutWriteQuoteCanvas)
    SquareFrameLayout mQuoteCanvas;

    private Context mContext;
    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private CanvasOptionsFragment mCanvasOptionsFragment;
    private ComponentImageViewOptionsFragment mComponentImageViewOptionsFragment;
    private ComponentTextViewOptionsFragment mComponentTextViewOptionsFragment;
    private android.support.v4.app.Fragment mActiveOptionFragment = null;

    private ComponentTextView mActiveComponentTextView;
    private ComponentImageView mActiveComponentImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quote);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mFragmentManager = getSupportFragmentManager();


        mQuoteCanvas.setOnTouchListener(new CanvasTouchListener());
        if (mActiveOptionFragment == null) {
            mActiveOptionFragment = new CanvasOptionsFragment();
        }

        setCanvasBackground();
        loadCanvasOptionsFragment();

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void loadCanvasOptionsFragment() {

        if (mCanvasOptionsFragment == null) {
            mCanvasOptionsFragment = new CanvasOptionsFragment();
            mCanvasOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_new_quote, mCanvasOptionsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadComponentImageViewOptionsFragment() {

        if (mComponentImageViewOptionsFragment == null) {
            mComponentImageViewOptionsFragment = new ComponentImageViewOptionsFragment();
            mComponentImageViewOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        }

        mComponentImageViewOptionsFragment.setComponentTextView(mActiveComponentImageView);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_new_quote, mComponentImageViewOptionsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadComponentTextViewOptionsFragment() {

        if (mComponentTextViewOptionsFragment == null) {
            mComponentTextViewOptionsFragment = new ComponentTextViewOptionsFragment();
            mCanvasOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_new_quote, mComponentTextViewOptionsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onComponentTextViewAdded(ComponentTextView componentTextView) {

        componentTextView.setOnTouchListener(new componentTextViewTouchListener());
        mActiveComponentTextView = componentTextView;
        loadComponentTextViewOptionsFragment();
    }

    @Override
    public void onComponentImageViewAdded(ComponentImageView componentImageView) {
        componentImageView.setOnTouchListener(new componentImageViewTouchListener());
        mActiveComponentImageView = componentImageView;
        loadComponentImageViewOptionsFragment();
    }

    private class CanvasTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            loadCanvasOptionsFragment();
            return false;
        }
    }

    private class componentTextViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View componentTextView, MotionEvent event) {
            mActiveComponentTextView = (ComponentTextView) componentTextView;
            loadComponentTextViewOptionsFragment();
            return false;
        }
    }

    private class componentImageViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View componentImageView, MotionEvent event) {
            mActiveComponentImageView = (ComponentImageView) componentImageView;
            loadComponentImageViewOptionsFragment();
            return false;
        }
    }

}
