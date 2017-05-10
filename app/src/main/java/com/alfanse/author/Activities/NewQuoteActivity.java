package com.alfanse.author.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.alfanse.author.CustomViews.ComponentImageView;
import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.ComponentView;
import com.alfanse.author.CustomViews.QuoteCanvas;
import com.alfanse.author.Fragments.CanvasOptionsFragment;
import com.alfanse.author.Fragments.ComponentImageViewOptionsFragment;
import com.alfanse.author.Fragments.ComponentTextViewOptionsFragment;
import com.alfanse.author.Models.CanvasTheme;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Fragments.CanvasOptionsFragment.CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID;
import static com.alfanse.author.Fragments.ComponentTextViewOptionsFragment.COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID;

public class NewQuoteActivity extends AppCompatActivity implements
        CanvasOptionsFragment.OnFragmentInteractionListener,
        ComponentTextViewOptionsFragment.OnFragmentInteractionListener,
        ComponentImageViewOptionsFragment.OnFragmentInteractionListener,
        ColorPickerDialogListener,
        ComponentView.onComponentViewInteractionListener {

    @BindView(R.id.SquareFrameLayoutWriteQuoteCanvas)
    QuoteCanvas mQuoteCanvas;

    private Context mContext;
    private Activity mActivity;

    private FragmentManager mFragmentManager;
    private CanvasOptionsFragment mCanvasOptionsFragment;
    private ComponentImageViewOptionsFragment mComponentImageViewOptionsFragment;
    private ComponentTextViewOptionsFragment mComponentTextViewOptionsFragment;
    private android.support.v4.app.Fragment mActiveOptionFragment = null;

    private ComponentTextView mActiveComponentTextView;
    private ComponentImageView mActiveComponentImageView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mCanvasThemesRef;
    private ArrayList<CanvasTheme> mListCanvasThemes = new ArrayList<CanvasTheme>();
    // Read from the database
    ValueEventListener CanvasThemesValueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            mListCanvasThemes.clear();
            CanvasTheme canvasTheme = null;
            for (DataSnapshot canvasThemesSnapshot : dataSnapshot.getChildren()) {
                canvasTheme = canvasThemesSnapshot.getValue(CanvasTheme.class);
                break;
            }
            mQuoteCanvas.setBackground(canvasTheme.getImageUrl());
            mQuoteCanvas.getBackground();
            addComponentTextView(canvasTheme);
            CommonView.getInstance(mContext).dismissProgressDialog();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quote);
        ButterKnife.bind(this);

        mContext = getApplicationContext();
        mActivity = NewQuoteActivity.this;
        mDatabase = FirebaseDatabase.getInstance();
        mFragmentManager = getSupportFragmentManager();

        mDatabase = FirebaseDatabase.getInstance();
        mCanvasThemesRef = mDatabase.getReference(Constants.CANVAS_THEME);

        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity);
        mCanvasThemesRef.addListenerForSingleValueEvent(CanvasThemesValueEventListener);

        mQuoteCanvas.setOnTouchListener(new CanvasTouchListener());
        if (mActiveOptionFragment == null) {
            mActiveOptionFragment = new CanvasOptionsFragment();
        }

        loadCanvasOptionsFragment();
    }

    private void loadCanvasOptionsFragment() {

        if (mCanvasOptionsFragment == null) {
            mCanvasOptionsFragment = new CanvasOptionsFragment();
            mCanvasOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_new_quote, mCanvasOptionsFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void loadComponentImageViewOptionsFragment() {

        mComponentImageViewOptionsFragment = new ComponentImageViewOptionsFragment();
        mComponentImageViewOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        mComponentImageViewOptionsFragment.setComponentImageView(mActiveComponentImageView);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_new_quote, mComponentImageViewOptionsFragment);
        transaction.commit();
    }

    public void loadComponentTextViewOptionsFragment() {


        mComponentTextViewOptionsFragment = new ComponentTextViewOptionsFragment();
        mComponentTextViewOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        mComponentTextViewOptionsFragment.setComponentTextView(mActiveComponentTextView);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_new_quote, mComponentTextViewOptionsFragment);
        transaction.commit();
    }

    @Override
    public void onComponentTextViewAdded(ComponentTextView componentTextView) {

        componentTextView.setOnTouchListener(new componentTextViewTouchListener());
        mActiveComponentTextView = componentTextView;
    }

    @Override
    public void onComponentImageViewAdded(ComponentImageView componentImageView) {
        componentImageView.setOnTouchListener(new componentImageViewTouchListener());
        mActiveComponentImageView = componentImageView;
    }

    @Override
    public void onFragmentBackPressed() {
        mQuoteCanvas.setStateFocused();
        loadCanvasOptionsFragment();
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID:
                mCanvasOptionsFragment.onColorSelected(dialogId, color);
                break;

            case COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID:
                mComponentTextViewOptionsFragment.onColorSelected(dialogId, color);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

        switch (dialogId) {
            case CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID:
                mCanvasOptionsFragment.onDialogDismissed(dialogId);
                break;

            case COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID:
                mComponentTextViewOptionsFragment.onDialogDismissed(dialogId);
                break;
        }

    }

    @Override
    public void onRemoveButtonClick() {
        mQuoteCanvas.setStateFocused();
        loadCanvasOptionsFragment();
    }

    private void addComponentTextView(CanvasTheme canvasTheme) {

        ComponentTextView textView = new ComponentTextView(mActivity, mQuoteCanvas);

        if (canvasTheme != null) {

            try {
                textView.setTypeface(FontHelper.getInstance(mContext).getFontsHashMap().get(canvasTheme.getTextFontFamily()).getFontTypeface());
            } catch (Exception e) {

            }

            textView.setTextStyle(Integer.parseInt(canvasTheme.getTextStyle()));
            textView.setTextSize(Float.parseFloat(canvasTheme.getTextSize()));
            textView.setTextColor(Color.parseColor(canvasTheme.getTextColor()));
            textView.setTextLocationX(Float.parseFloat(canvasTheme.getTextLocationX()));
            textView.setTextLocationY(Float.parseFloat(canvasTheme.getTextLocationY()));
        } else {
            textView.setTextStyle(Typeface.BOLD);
            textView.setTextSize(getResources().getDimension(R.dimen.font_small));
            textView.setTextLocationX((float) 20);
            textView.setTextLocationY((float) 20);
        }

        textView.setTag(Constants.TAG_DEFAULT_CANVASE_TEXT_VIEW);

        textView.setMinimumHeight(textView.getMinimumHeight());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (mQuoteCanvas.getWidth() * .7), FrameLayout.LayoutParams.WRAP_CONTENT);

        mQuoteCanvas.addView(textView, layoutParams);

        onComponentTextViewAdded(textView);

    }

    private class CanvasTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mQuoteCanvas.setStateFocused();
                loadCanvasOptionsFragment();
            }
            return false;
        }
    }

    private class componentTextViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View componentTextView, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mActiveComponentTextView = (ComponentTextView) componentTextView;
                loadComponentTextViewOptionsFragment();
            }
            return false;
        }
    }

    private class componentImageViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View componentImageView, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mActiveComponentImageView = (ComponentImageView) componentImageView;
                loadComponentImageViewOptionsFragment();
            }
            return false;
        }
    }
}
