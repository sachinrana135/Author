/*
 * Copyright (c) 2018. Alfanse Developers
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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfanse.author.Interfaces.onFilterItemClickListener;
import com.alfanse.author.Models.Filter;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.GPUImageFilterTools;
import com.alfanse.author.Utilities.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by Velocity-1601 on 4/19/2017.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private final onFilterItemClickListener listener;
    private Context mContext;
    private ArrayList<Filter> mListFilters;
    private Handler handler;
    private GPUImage mImage;
    private Bitmap originalBitmap = null;

    public FilterAdapter(Context context, ArrayList<Filter> listFilters, onFilterItemClickListener listener) {
        mContext = context;
        mListFilters = listFilters;
        this.listener = listener;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);

        return new FilterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilterViewHolder holder, int position) {
        holder.bind(mListFilters.get(position), listener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListFilters.size();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.filter_item_filter)
        ImageView filterImage;
        @BindView(R.id.title_item_filter)
        TextView titleFilter;

        private Boolean isAdjustable;


        public FilterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Filter filter, final onFilterItemClickListener listener) {

            titleFilter.setText(filter.getTitle());

            if (filter.isSelected()) {
                titleFilter.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            } else {
                titleFilter.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            }

            RequestOptions filterImageOptions = new RequestOptions()
                    .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                    .fitCenter()
                    .placeholder(R.drawable.quote_placeholder)
                    .centerCrop();

            Glide.with(mContext)
                    .asBitmap()
                    .load(R.drawable.filter_sample)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(final Bitmap resource, Transition<? super Bitmap> transition) {
                            filterImage.setImageBitmap(resource);
                            if (filter.getFilter() != null) {
                                if (originalBitmap == null) {
                                    originalBitmap = ((BitmapDrawable) filterImage.getDrawable()).getBitmap();
                                }

                                GPUImageFilter gpuImageFilter = null;
                                try {
                                    gpuImageFilter = GPUImageFilterTools.createFilterForType(mContext, filter.getFilter());
                                } catch (IllegalStateException e) {
                                    Log.d("Filter exception", e.getMessage());
                                }
                                filterImage.setImageBitmap(getBitmapWithFilterApplied(originalBitmap, gpuImageFilter));
                            }
                        }
                    });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(filter);
                }
            });
        }

    }

    public Bitmap getBitmapWithFilterApplied(Bitmap bitmap, GPUImageFilter filter) {
        if (mImage == null) {
            mImage = new GPUImage(mContext);
        }
        mImage.setFilter(filter);
        return mImage.getBitmapWithFilterApplied(bitmap);
    }
}
