package com.alfanse.author.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alfanse.author.Adapters.CanvasThemesAdapter;
import com.alfanse.author.CustomViews.ComponentBoxView;
import com.alfanse.author.CustomViews.ComponentImageView;
import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.QuoteCanvas;
import com.alfanse.author.Models.CanvasTheme;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CanvasOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CanvasOptionsFragment extends Fragment implements ColorPickerDialogListener {

    public static final int CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID = 100;
    private static final int ALL_PERMISSIONS_REQUEST_CODE = 1000;
    private static final String IMAGE_REQUIRED_FOR_CANVAS = "IMAGE_REQUIRED_FOR_CANVAS";
    private static final String IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW = "IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW";

    @BindView(R.id.layout_colorize_fragment_canvas_options)
    LinearLayout optionColorize;
    @BindView(R.id.layout_gallery_fragment_canvas_options)
    LinearLayout optionGallery;
    @BindView(R.id.layout_add_photo_fragment_canvas_options)
    LinearLayout optionAddImage;
    @BindView(R.id.layout_add_text_fragment_canvas_options)
    LinearLayout optionAddText;
    @BindView(R.id.layout_add_box_fragment_canvas_options)
    LinearLayout optionAddBox;
    @BindView(R.id.rv_canvas_themes_fragment_canvas_options)
    RecyclerView recyclerViewCanvasThemes;

    private Context mContext;
    private Activity mActivity;
    private QuoteCanvas mCanvas;
    private Uri mCropImageUri;
    private CanvasThemesAdapter mCanvasThemesAdapter;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mCanvasThemesRef;
    private ArrayList<CanvasTheme> mListCanvasThemes = new ArrayList<CanvasTheme>();
    // Read from the database
    ValueEventListener CanvasThemesValueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            mListCanvasThemes.clear();
            for (DataSnapshot canvasThemesSnapshot : dataSnapshot.getChildren()) {
                CanvasTheme canvasTheme = canvasThemesSnapshot.getValue(CanvasTheme.class);
                mListCanvasThemes.add(canvasTheme);
            }
            mCanvasThemesAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private OnFragmentInteractionListener mListener;
    private ComponentTextView mActiveComponentTextView;
    private LinearLayoutManager mLinearLayoutManager;
    private CanvasTheme defaultCanvasTheme;
    private int firstIndex = 0;
    private String imageRequiredFor = null;

    public CanvasOptionsFragment() {
        mDatabase = FirebaseDatabase.getInstance();
        mCanvasThemesRef = mDatabase.getReference(Constants.FIREBASE_REFERENCE_CANVAS_THEMES);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCanvasThemesRef.addListenerForSingleValueEvent(CanvasThemesValueEventListener);
        mCanvasThemesAdapter = new CanvasThemesAdapter(mContext, mListCanvasThemes, new CanvasThemesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CanvasTheme canvasTheme) {
                mCanvas.setBackground(canvasTheme.getImageUrl());

                if (mCanvas.findViewWithTag(Constants.TAG_DEFAULT_CANVASE_TEXT_VIEW) != null) {
                    ComponentTextView componentTextView = (ComponentTextView) mCanvas.findViewWithTag(Constants.TAG_DEFAULT_CANVASE_TEXT_VIEW);
                    componentTextView.setTheme(canvasTheme);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_canvas_options, container, false);
        ButterKnife.bind(this, view);
        mLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCanvasThemes.setLayoutManager(mLinearLayoutManager);
        recyclerViewCanvasThemes.setAdapter(mCanvasThemesAdapter);
        initOptionItemClickListener();
        return view;
    }

    private void initOptionItemClickListener() {

        optionColorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowPresets(true)
                        .setDialogId(CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorBlack))
                        .setShowAlphaSlider(false)
                        .show(mActivity);
            }
        });

        optionGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRequiredFor = IMAGE_REQUIRED_FOR_CANVAS;
                startPickImageActivity();
            }
        });

        optionAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComponentTextView();
            }
        });

        optionAddBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComponentBoxView();
            }
        });

        optionAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRequiredFor = IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW;
                startPickImageActivity();
            }
        });

    }

    @SuppressLint("NewApi")
    private void startPickImageActivity() {

        // For API >= 23 we need to check specifically that we have permissions to read external storage,
        // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.

        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (!Utils.getInstance(mContext).hasPermissions(PERMISSIONS)) {
            // request permissions and handle the result in onRequestPermissionsResult()
            requestPermissions(PERMISSIONS, ALL_PERMISSIONS_REQUEST_CODE);
        } else {
            startActivityForResult(CropImage.getPickImageChooserIntent(mContext), PICK_IMAGE_CHOOSER_REQUEST_CODE);
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

    public void setQuoteCanvas(QuoteCanvas quoteCanvas) {
        mCanvas = quoteCanvas;
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        mCanvas.setBackground(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void startCropImageActivity(Uri imageUri) {
        if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_CANVAS)) {
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(mContext, this); // Need square image
        } else if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW)) {
            CropImage.activity(imageUri).start(mContext, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(CropImage.getPickImageChooserIntent(mContext), PICK_IMAGE_CHOOSER_REQUEST_CODE);
                } else {
                    // TODO show message
                }
                break;
            }
            case CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE: {
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startCropImageActivity(mCropImageUri);
                } else {
                    // TODO show message
                }
                break;
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = CropImage.getPickImageResultUri(mActivity, data);

                    // For API >= 23 we need to check specifically that we have permissions to read external storage.
                    if (CropImage.isReadExternalStoragePermissionsRequired(mActivity, imageUri)) {
                        // request permissions and handle the result in onRequestPermissionsResult()
                        mCropImageUri = imageUri;
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                    } else {
                        // no permissions required or already grunted, can start crop image activity
                        startCropImageActivity(imageUri);
                    }
                }
                break;
            }

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK) {

                    if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_CANVAS)) {
                        Uri croppedImageUri = result.getUri();
                        mCanvas.setBackground(croppedImageUri);
                    } else if (imageRequiredFor.equalsIgnoreCase(IMAGE_REQUIRED_FOR_COMPONENT_IMAGEVIEW)) {
                        addComponentImageView(result.getUri());
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(mActivity, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void addComponentTextView() {

        ComponentTextView textView = new ComponentTextView(mActivity, mCanvas);
        textView.setTheme(CanvasTheme.getDefaultTheme());

        textView.setMinimumHeight(textView.getMinimumHeight());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (mCanvas.getWidth() * .7), FrameLayout.LayoutParams.WRAP_CONTENT);

        mCanvas.addView(textView, layoutParams);

        if (mListener != null) {
            mListener.onComponentTextViewAdded(textView);
        }
    }

    private void addComponentBoxView() {

        ComponentBoxView boxView = new ComponentBoxView(mActivity, mCanvas);

        boxView.setMinimumHeight(boxView.getMinimumHeight());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mCanvas.getWidth() / 2, mCanvas.getHeight() / 2);

        mCanvas.addView(boxView, layoutParams);

        if (mListener != null) {
            mListener.onComponentBoxViewAdded(boxView);
        }
    }

    private void addComponentImageView(Uri imageUri) {

        ComponentImageView imageView = new ComponentImageView(mActivity, mCanvas, imageUri);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        mCanvas.addView(imageView, layoutParams);

        if (mListener != null) {
            mListener.onComponentImageViewAdded(imageView);
        }

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

        void onComponentImageViewAdded(ComponentImageView componentImageView);

        void onComponentBoxViewAdded(ComponentBoxView boxView);
    }


}
