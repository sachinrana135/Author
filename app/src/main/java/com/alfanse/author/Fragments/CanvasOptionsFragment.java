package com.alfanse.author.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
 * {@link CanvasOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CanvasOptionsFragment extends Fragment {

    @BindView(R.id.image_colorize_fragment_canvas_options) ImageView optionColorize;
    @BindView(R.id.image_add_photo_fragment_canvas_options) ImageView optionAddImage;
    @BindView(R.id.image_add_text_fragment_canvas_options) ImageView optionAddText;
    @BindView(R.id.image_add_sticker_fragment_canvas_options) ImageView optionAddSticker;

    private Context mContext;
    private Activity mActivity;
    private SquareFrameLayout mCanvas;

    private OnFragmentInteractionListener mListener;
    private ComponentTextView mActiveComponentTextView;

    public CanvasOptionsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_canvas_options, container, false);
        ButterKnife.bind(this,view);

        initOptionItemClickListener();

        return view;
    }

    private void initOptionItemClickListener() {

        optionAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ComponentTextView textView = new ComponentTextView(mContext, mCanvas, getString(R.string.text_default_quote_fragment_write_quote_fragment));

                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (mCanvas.getWidth() * 0.7), FrameLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.gravity = Gravity.START;

                mCanvas.addView(textView,layoutParams);

                if (mListener != null) {
                    mListener.onAddComponentTextView(textView);
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

                    Double ratio = ((double) imageWidth) /  ((double) imageHeight );

                    int final_width;

                    int final_height;

                    final_height   =  (int) (mCanvas.getHeight() * 0.7);

                    final_width = (int) (final_height * ratio );

                    ComponentImageView imageView = new ComponentImageView(mContext,mCanvas, bitmap);

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(final_width, final_height);

                    mCanvas.addView(imageView,layoutParams);

                    if (mListener != null) {
                        mListener.onAddComponentImageView(imageView);
                    }

                } catch (Exception e) {

                }

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
        void onAddComponentTextView(ComponentTextView componentTextView);
        void onAddComponentImageView(ComponentImageView componentImageView);
    }
}
