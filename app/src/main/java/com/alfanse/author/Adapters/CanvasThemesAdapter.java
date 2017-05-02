package com.alfanse.author.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alfanse.author.Models.CanvasTheme;
import com.alfanse.author.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Velocity-1601 on 4/19/2017.
 */

public class CanvasThemesAdapter extends RecyclerView.Adapter<CanvasThemesAdapter.CanvasThemeViewHolder> {

    private Context mContext;
    private ArrayList<CanvasTheme> mListCanvasThemes;

    public CanvasThemesAdapter() {

    }

    public CanvasThemesAdapter(Context context, ArrayList<CanvasTheme> listCanvasThemes) {
        mContext = context;
        mListCanvasThemes = listCanvasThemes;
    }

    @Override
    public CanvasThemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_canvas_image, parent, false);

        return new CanvasThemeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CanvasThemeViewHolder holder, int position) {
        CanvasTheme canvasTheme = mListCanvasThemes.get(position);
        Picasso.with(mContext)
                .load(canvasTheme.getImageUrl())
                .fit()
                .into(holder.canvasThemeImage);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListCanvasThemes.size();
    }

    public class CanvasThemeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.canvas_theme_image_item_canvas_image)
        ImageView canvasThemeImage;

        public CanvasThemeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
