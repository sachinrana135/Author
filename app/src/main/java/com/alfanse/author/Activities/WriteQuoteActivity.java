package com.alfanse.author.Activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alfanse.author.CustomViews.SquareFrameLayout;
import com.alfanse.author.R;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WriteQuoteActivity extends AppCompatActivity {

    @BindView(R.id.SquareFrameLayoutWriteQuoteCanvas) SquareFrameLayout quoteCanvas;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_quote);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        setCanvasBackground();

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
        quoteCanvas.setBackgroundImage(bitmap);
    }
}
