/*
 * Copyright (c) 2017. Alfanse Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.alfanse.author.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alfanse.author.Interfaces.onCanvasThemeItemClickListener;
import com.alfanse.author.Models.CanvasTheme;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Velocity-1601 on 4/19/2017.
 */

public class CanvasThemesAdapter extends RecyclerView.Adapter<CanvasThemesAdapter.CanvasThemeViewHolder> {

    private final onCanvasThemeItemClickListener listener;
    private Context mContext;
    private ArrayList<CanvasTheme> mListCanvasThemes;

    public CanvasThemesAdapter(Context context, ArrayList<CanvasTheme> listCanvasThemes, onCanvasThemeItemClickListener listener) {
        mContext = context;
        mListCanvasThemes = listCanvasThemes;
        this.listener = listener;
    }

    @Override
    public CanvasThemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_canvas_image, parent, false);

        return new CanvasThemeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CanvasThemeViewHolder holder, int position) {
        holder.bind(mListCanvasThemes.get(position), listener);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListCanvasThemes.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CanvasTheme canvasTheme);
    }

    public class CanvasThemeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.canvas_theme_image_item_canvas_image)
        ImageView canvasThemeImage;
        @BindView(R.id.progress_bar_item_canvas_image)
        ProgressBar canvasThemeProgressBar;


        public CanvasThemeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final CanvasTheme canvasTheme, final onCanvasThemeItemClickListener listener) {

            RequestOptions canvasImageOptions = new RequestOptions()
                    .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                    .fitCenter()
                    .placeholder(R.drawable.quote_placeholder)
                    .centerCrop();

            Glide.with(mContext)
                    .load(canvasTheme.getImageUrl())
                    .apply(canvasImageOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            canvasThemeProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            canvasThemeProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(canvasThemeImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(canvasTheme);
                }
            });
        }
    }
}
