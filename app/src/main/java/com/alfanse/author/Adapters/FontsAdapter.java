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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alfanse.author.Models.Font;
import com.alfanse.author.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Velocity-1601 on 4/19/2017.
 */

public class FontsAdapter extends RecyclerView.Adapter<FontsAdapter.FontViewHolder> {

    private final OnItemClickListener listener;
    private Context mContext;
    private ArrayList<Font> mListFont;

    public FontsAdapter(Context context, ArrayList<Font> listFont, OnItemClickListener listener) {
        mContext = context;
        mListFont = listFont;
        this.listener = listener;

    }

    @Override
    public FontViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_font, parent, false);

        return new FontViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FontViewHolder holder, int position) {
        holder.bind(mListFont.get(position), listener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListFont.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Font font);
    }

    public class FontViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.font_text_item_font)
        TextView fontText;
        @BindView(R.id.font_name_item_font)
        TextView fontName;

        public FontViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Font font, final OnItemClickListener listener) {

            fontText.setTypeface(font.getFontTypeface());
            fontName.setText(font.getFontName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(font);
                }
            });
        }
    }
}
