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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alfanse.author.Interfaces.onCommentItemClickListener;
import com.alfanse.author.Models.Comment;
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

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private final onCommentItemClickListener listener;
    private Context mContext;
    private ArrayList<Comment> mListComments;

    public CommentsAdapter(Context context, ArrayList<Comment> listComments, onCommentItemClickListener listener) {
        mContext = context;
        mListComments = listComments;
        this.listener = listener;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.bind(mListComments.get(position), listener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListComments.size();
    }

    public void showMenuPopup(View v, final Comment comment) {
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_item_comment, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_report_quote_item_comment:
                        listener.onActionReportClick(comment);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        popupMenu.show();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_bar_author_image_item_comment)
        ProgressBar progressBarLoadingAuthorImage;
        @BindView(R.id.image_author_image_item_comment)
        ImageView imageAuthor;
        @BindView(R.id.text_author_name_item_comment)
        TextView textAuthorName;
        @BindView(R.id.text_comment_item_comment)
        TextView textComment;
        @BindView(R.id.text_date_comment_item_comment)
        TextView textDateComment;
        @BindView(R.id.image_more_options_item_comment)
        ImageView imageMoreOptions;

        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Comment comment, final onCommentItemClickListener listener) {

            RequestOptions authorImageOptions = new RequestOptions()
                    .fitCenter()
                    .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                    .circleCrop();

            Glide.with(mContext)
                    .load(comment.getAuthor().getProfileImage())
                    .apply(authorImageOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBarLoadingAuthorImage.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBarLoadingAuthorImage.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageAuthor);

            textAuthorName.setText(comment.getAuthor().getName());
            textComment.setText(comment.getComment());
            textDateComment.setText(comment.getDateAdded());

            textAuthorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAuthorClick(comment);
                }
            });

            imageAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAuthorClick(comment);
                }
            });

            imageMoreOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenuPopup(v, comment);
                }
            });


        }
    }

}
