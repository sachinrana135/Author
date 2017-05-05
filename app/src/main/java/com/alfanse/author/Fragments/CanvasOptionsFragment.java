package com.alfanse.author.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.system.ErrnoException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.alfanse.author.Adapters.CanvasThemesAdapter;
import com.alfanse.author.CustomViews.ComponentImageView;
import com.alfanse.author.CustomViews.ComponentTextView;
import com.alfanse.author.CustomViews.SquareFrameLayout;
import com.alfanse.author.Models.CanvasTheme;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Build.VERSION_CODES.KITKAT;
import static android.support.v4.content.FileProvider.getUriForFile;
import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CanvasOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CanvasOptionsFragment extends Fragment implements ColorPickerDialogListener {

    public static final int CANVAS_OPTIONS_COLOR_PICKER_DIALOG_ID = 100;
    private static final int REQUEST_CODE_PICK_CANVAS_IMAGE = 1000;
    private static final int REQUEST_CODE_CROP_CANVAS_IMAGE = 1001;
    private static final int REQUEST_CODE_READ_WRITE_EXTERNAL_STORAGE = 1050;
    private static final String FILE_PROVIDER_AUTHORITY = "com.alfanse.author.fileprovider";

    @BindView(R.id.image_colorize_fragment_canvas_options)
    ImageView optionColorize;
    @BindView(R.id.image_gallery_fragment_canvas_options)
    ImageView optionGallery;
    @BindView(R.id.image_add_photo_fragment_canvas_options)
    ImageView optionAddImage;
    @BindView(R.id.image_add_text_fragment_canvas_options)
    ImageView optionAddText;
    @BindView(R.id.image_add_sticker_fragment_canvas_options)
    ImageView optionAddSticker;
    @BindView(R.id.rv_canvas_themes_fragment_canvas_options)
    RecyclerView recyclerViewCanvasThemes;

    private Context mContext;
    private Activity mActivity;
    private SquareFrameLayout mCanvas;
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

    public CanvasOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
        mCanvasThemesRef = mDatabase.getReference(Constants.CANVAS_THEME);
        mCanvasThemesRef.addValueEventListener(CanvasThemesValueEventListener);
        mCanvasThemesAdapter = new CanvasThemesAdapter(mContext, mListCanvasThemes);
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
                // For API >= 23 we need to check specifically that we have permissions to read external storage,
                // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, REQUEST_CODE_READ_WRITE_EXTERNAL_STORAGE);
                } else {
                    startActivityForResult(getPickImageChooserIntent(), REQUEST_CODE_PICK_CANVAS_IMAGE);
                }*/

                startActivityForResult(CropImage.getPickImageChooserIntent(mContext), PICK_IMAGE_CHOOSER_REQUEST_CODE);

            }
        });


        optionAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ComponentTextView textView = new ComponentTextView(mContext, mCanvas, getString(R.string.text_default_quote_fragment_write_quote_fragment));

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (mCanvas.getWidth() * 0.7), FrameLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.gravity = Gravity.START;

                mCanvas.addView(textView, layoutParams);

                if (mListener != null) {
                    mListener.onComponentTextViewAdded(textView);
                }

            }
        });

        optionAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AssetManager assetManager = mContext.getAssets();
                InputStream is = null;
                try {
                    is = assetManager.open("image/pic1.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // mBitmap = BitmapFactory.decodeStream(is);
                Drawable mDrawable = Drawable.createFromStream(is, null);

                Bitmap bitmap = ((BitmapDrawable) mDrawable).getBitmap();

                try {

                    int imageWidth = bitmap.getWidth();
                    int imageHeight = bitmap.getHeight();

                        /*float imageWidth = (float)drawable.getIntrinsicWidth();
                        float imageHeight = (float)drawable.getIntrinsicHeight();*/

                    Double ratio = ((double) imageWidth) / ((double) imageHeight);

                    int final_width;

                    int final_height;

                    final_height = (int) (mCanvas.getHeight() * 0.7);

                    final_width = (int) (final_height * ratio);

                    ComponentImageView imageView = new ComponentImageView(mContext, mCanvas, bitmap);

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(final_width, final_height);

                    mCanvas.addView(imageView, layoutParams);

                    if (mListener != null) {
                        mListener.onComponentImageViewAdded(imageView);
                    }

                } catch (Exception e) {

                }

            }
        });

        optionAddSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    }

    public void setQuoteCanvas(SquareFrameLayout quoteCanvas) {
        mCanvas = quoteCanvas;
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        Toast.makeText(mActivity, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri).setAspectRatio(1, 1)
                .start(mContext, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(mActivity, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }



        /*switch (requestCode) {
            case REQUEST_CODE_READ_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivityForResult(getPickImageChooserIntent(), REQUEST_CODE_PICK_CANVAS_IMAGE);

                } else {
                    Toast.makeText(mActivity, "Required permissions are not granted", Toast.LENGTH_LONG).show();
                }

                break;
            }
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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

        /*if (resultCode == Activity.RESULT_OK) {



            switch (requestCode) {

                case REQUEST_CODE_PICK_CANVAS_IMAGE: {

                    Uri pickImageUri = getPickImageResultUri(data);
                    performCrop(pickImageUri, REQUEST_CODE_CROP_CANVAS_IMAGE);
                    break;
                }

                case REQUEST_CODE_CROP_CANVAS_IMAGE: {

                    Uri cropImageUri = getPickImageResultUri(data);

                    break;
                }
            }

        }*/
    }

    private void performCrop(Uri imageUri, int requestCode) {
        try {


            Uri outputFileUri = getCaptureImageOutputUri1();

            mContext.grantUriPermission("com.google.android.apps.photos", outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            //indicate image type and Uri
            cropIntent.setData(imageUri);

            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //indicate aspect of desired crop
            cropIntent.putExtra("scale", true); // boolean
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //you must setup this
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Create a chooser intent to select the  source to get image from.<br/>
     * The source can be camera's  (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the  intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to  save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = mContext.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            mContext.grantUriPermission(res.activityInfo.packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent;
        if (Build.VERSION.SDK_INT < KITKAT) {
            galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
            for (ResolveInfo res : listGallery) {
                Intent intent = new Intent(galleryIntent);
                mContext.grantUriPermission(res.activityInfo.packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                allIntents.add(intent);
            }
        } else {
            galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
            galleryIntent.setType("image/*");
            allIntents.add(galleryIntent);
        }

        // Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {

        Uri outputFileUri = null;
        File getImage = mContext.getExternalCacheDir();
        File newFile = new File(getImage.getPath(), "pickImageResult.jpeg");
        if (getImage != null) {
            outputFileUri = getUriForFile(mContext, FILE_PROVIDER_AUTHORITY, newFile);
        }
        return outputFileUri;
    }

    private Uri getCaptureImageOutputUri1() {

        Uri outputFileUri = null;
        File getImage = mContext.getExternalFilesDir(null);
        File newFile = new File(getImage.getPath(), "pickImageResult1.jpeg");
        if (getImage != null) {
            outputFileUri = getUriForFile(mContext, FILE_PROVIDER_AUTHORITY, newFile);
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from  {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera  and gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = mContext.getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
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
    }
}
