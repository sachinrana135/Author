package com.alfanse.author.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alfanse.author.CustomViews.ComponentBoxView;
import com.alfanse.author.CustomViews.ComponentImageView;
import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.ComponentView;
import com.alfanse.author.CustomViews.DialogBuilder;
import com.alfanse.author.CustomViews.QuoteCanvas;
import com.alfanse.author.Fragments.CanvasOptionsFragment;
import com.alfanse.author.Fragments.ComponentBoxViewOptionsFragment;
import com.alfanse.author.Fragments.ComponentImageViewOptionsFragment;
import com.alfanse.author.Fragments.ComponentTextViewOptionsFragment;
import com.alfanse.author.Models.CanvasTheme;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.CommonMethod;
import com.alfanse.author.Utilities.CommonView;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.FontHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alfanse.author.Fragments.CanvasOptionsFragment.CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID;
import static com.alfanse.author.Fragments.ComponentBoxViewOptionsFragment.COMPONENT_BOXVIEW_OPTIONS_BG_COLOR_PICKER_DIALOG_ID;
import static com.alfanse.author.Fragments.ComponentTextViewOptionsFragment.COMPONENT_TEXTVIEW_OPTIONS_COLOR_PICKER_DIALOG_ID;

public class NewQuoteActivity extends AppCompatActivity implements
        CanvasOptionsFragment.OnFragmentInteractionListener,
        ComponentTextViewOptionsFragment.OnFragmentInteractionListener,
        ComponentImageViewOptionsFragment.OnFragmentInteractionListener,
        ComponentBoxViewOptionsFragment.OnFragmentInteractionListener,
        ColorPickerDialogListener,
        ComponentView.onComponentViewInteractionListener {

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 32423;
    @BindView(R.id.toolbar_new_quote)
    Toolbar mToolbar;
    @BindView(R.id.SquareFrameLayoutWriteQuoteCanvas)
    QuoteCanvas mQuoteCanvas;
    @BindView(R.id.container_new_quote)
    LinearLayout container;

    private Context mContext;
    private Activity mActivity;
    private GestureDetectorCompat mComponentTextViewGestureDetector;


    private FragmentManager mFragmentManager;
    private CanvasOptionsFragment mCanvasOptionsFragment;
    private ComponentImageViewOptionsFragment mComponentImageViewOptionsFragment;
    private ComponentTextViewOptionsFragment mComponentTextViewOptionsFragment;
    private ComponentBoxViewOptionsFragment mComponentBoxViewOptionsFragment;
    private Fragment mActiveOptionFragment = null;

    private ComponentTextView mActiveComponentTextView;
    private ComponentImageView mActiveComponentImageView;
    private ComponentBoxView mActiveComponentBoxView;
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

    private Runnable fontsLoaderTask = new Runnable() {
        @Override
        public void run() {
            FontHelper.getInstance(mContext);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quote);
        ButterKnife.bind(this);
        initToolbar();
        mComponentTextViewGestureDetector = new GestureDetectorCompat(this, new ComponentTextViewGestureListener());

        mContext = getApplicationContext();
        mActivity = NewQuoteActivity.this;
        mDatabase = FirebaseDatabase.getInstance();
        mFragmentManager = getFragmentManager();

        mDatabase = FirebaseDatabase.getInstance();
        mCanvasThemesRef = mDatabase.getReference(Constants.CANVAS_THEME);

        CommonView.getInstance(mContext).showTransparentProgressDialog(mActivity);
        mCanvasThemesRef.addListenerForSingleValueEvent(CanvasThemesValueEventListener);
        fontsLoaderTask.run();

        mQuoteCanvas.setOnTouchListener(new CanvasTouchListener());
        if (mActiveOptionFragment == null) {
            mActiveOptionFragment = new CanvasOptionsFragment();
        }

        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            container.setOrientation(LinearLayout.HORIZONTAL);
        } else if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            container.setOrientation(LinearLayout.VERTICAL);
        }

        loadCanvasOptionsFragment();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_new_quote));
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
        DialogBuilder builder = new DialogBuilder(mActivity);
        // Add the buttons
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.msg_exit_confirm);
        builder.setDialogType(DialogBuilder.WARNING);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void loadComponentImageViewOptionsFragment() {

        mComponentImageViewOptionsFragment = new ComponentImageViewOptionsFragment();
        mComponentImageViewOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        mComponentImageViewOptionsFragment.setComponentImageView(mActiveComponentImageView);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_new_quote, mComponentImageViewOptionsFragment);
        transaction.commit();
    }

    public void loadComponentBoxViewOptionsFragment() {

        mComponentBoxViewOptionsFragment = new ComponentBoxViewOptionsFragment();
        mComponentBoxViewOptionsFragment.setQuoteCanvas(mQuoteCanvas);
        mComponentBoxViewOptionsFragment.setComponentBoxView(mActiveComponentBoxView);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.option_container_new_quote, mComponentBoxViewOptionsFragment);
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
    public void onComponentBoxViewAdded(ComponentBoxView componentBoxView) {

        componentBoxView.setOnTouchListener(new componentBoxViewTouchListener());
        mActiveComponentBoxView = componentBoxView;
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

            case COMPONENT_BOXVIEW_OPTIONS_BG_COLOR_PICKER_DIALOG_ID:
                mComponentBoxViewOptionsFragment.onColorSelected(dialogId, color);
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

            case COMPONENT_BOXVIEW_OPTIONS_BG_COLOR_PICKER_DIALOG_ID:
                mComponentBoxViewOptionsFragment.onDialogDismissed(dialogId);
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
            textView.setTheme(canvasTheme);
        } else {
            textView.setTheme(CanvasTheme.getDefaultTheme());
        }

        textView.setTag(Constants.TAG_DEFAULT_CANVASE_TEXT_VIEW);

        textView.setMinimumHeight(textView.getMinimumHeight());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (mQuoteCanvas.getWidth() * .7), FrameLayout.LayoutParams.WRAP_CONTENT);

        mQuoteCanvas.addView(textView, layoutParams);

        onComponentTextViewAdded(textView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_reset:
                showResetWarningDialog();
                return true;

            case R.id.action_done:

                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                } else {
                    saveCanvasIntoImage();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(menuItem);

        }
    }

    private void saveCanvasIntoImage() {

        Bitmap bitmap = Bitmap.createBitmap(mQuoteCanvas.getWidth(), mQuoteCanvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mQuoteCanvas.draw(canvas);
        try {

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.QUOTE_PUBLIC_OUTPUT_DIRECTORY);

            if (!dir.mkdirs()) {
                Log.e("Error", "Directory not created");
            }

            File file = new File(dir.getAbsolutePath() + "/" + CommonMethod.getTimeStamp() + Constants.QUOTE_OUTPUT_FORMAT);

            FileOutputStream output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showResetWarningDialog() {

        DialogBuilder builder = new DialogBuilder(mActivity);
        // Add the buttons
        builder.setPositiveButton(R.string.action_reset, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(getIntent());
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.msg_reset_confirm);
        builder.setDialogType(DialogBuilder.WARNING);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_quote, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    saveCanvasIntoImage();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            container.setOrientation(LinearLayout.HORIZONTAL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            container.setOrientation(LinearLayout.VERTICAL);
        }
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
            mActiveComponentTextView = (ComponentTextView) componentTextView;
            return mComponentTextViewGestureDetector.onTouchEvent(event);
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

    private class componentBoxViewTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View componentBoxView, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mActiveComponentBoxView = (ComponentBoxView) componentBoxView;
                loadComponentBoxViewOptionsFragment();
            }
            return false;
        }
    }

    public class ComponentTextViewGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            loadComponentTextViewOptionsFragment();
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            ContextThemeWrapper ctx = new ContextThemeWrapper(mContext, R.style.AppTheme);
            LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            final View view = layoutInflater.inflate(R.layout.layout_edit_text_dialog, null);
            final EditText quoteTextView = (EditText) view.findViewById(R.id.edit_text_layout_edit_text_dialog);
            quoteTextView.setText(mActiveComponentTextView.getText());

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            // Add the buttons
            builder.setPositiveButton(R.string.action_done, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // DO nothing
                }
            });
            builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            // Set other dialog properties
            builder.setView(view);
            // Create the AlertDialog
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quoteTextView.setError(null);
                    if (quoteTextView.getText().toString().isEmpty()) {
                        quoteTextView.setError(getString(R.string.error_required));
                    } else {
                        mActiveComponentTextView.setText(quoteTextView.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
            return false;
        }
    }
}
