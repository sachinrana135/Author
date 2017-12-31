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

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alfanse.author.Interfaces.onAuthorFollowedListener;
import com.alfanse.author.Interfaces.onQuoteItemClickListener;
import com.alfanse.author.Interfaces.onQuoteLikedListener;
import com.alfanse.author.Models.Author;
import com.alfanse.author.Models.Quote;
import com.alfanse.author.R;
import com.alfanse.author.Utilities.Constants;
import com.alfanse.author.Utilities.SharedManagement;
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

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {

    private final onQuoteItemClickListener listener;
    private final Author mLoggedAuthor;
    private Context mContext;
    private ArrayList<Quote> mListQuotes;
    private ObjectAnimator mObjectAnimator;

    public QuotesAdapter(Context context, ArrayList<Quote> listQuotes, onQuoteItemClickListener listener) {
        mContext = context;
        mListQuotes = listQuotes;
        this.listener = listener;
        mLoggedAuthor = SharedManagement.getInstance(mContext).getLoggedUser();
    }

    @Override
    public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);

        return new QuoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuoteViewHolder holder, int position) {
        holder.bind(mListQuotes.get(position), listener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mListQuotes.size();
    }

    public void showMenuPopup(View v, final Quote quote) {
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_item_quote, popupMenu.getMenu());
        MenuItem followItem = popupMenu.getMenu().findItem(R.id.action_follow_author_item_quote);
        MenuItem deleteItem = popupMenu.getMenu().findItem(R.id.action_delete_quote_item_quote);

        // Hide follow option if user is viewing his quote
        if (quote.getAuthor().getId().equalsIgnoreCase(mLoggedAuthor.getId())) {
            followItem.setVisible(false);
            deleteItem.setVisible(true);
        }

        if (quote.getAuthor().isFollowingAuthor()) {
            followItem.setTitle(mContext.getString(R.string.action_unfollow));
        } else {
            followItem.setTitle(mContext.getString(R.string.action_follow));
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_follow_author_item_quote:
                        listener.onActionFollowClick(quote, new onAuthorFollowedListener() {
                            @Override
                            public void onAuthorFollowed() {
                                if (quote.getAuthor().isFollowingAuthor()) {
                                    item.setTitle(mContext.getString(R.string.action_follow));
                                    quote.getAuthor().setFollowingAuthor(false);
                                } else {
                                    item.setTitle(mContext.getString(R.string.action_unfollow));
                                    quote.getAuthor().setFollowingAuthor(true);
                                }
                            }
                        });
                        break;
                    case R.id.action_download_quote_item_quote:
                        listener.onActionDownloadClick(quote);
                        break;
                    case R.id.action_report_quote_item_quote:
                        listener.onActionReportClick(quote);
                        break;
                    case R.id.action_delete_quote_item_quote:
                        listener.onActionDeleteClick(quote);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        popupMenu.show();
    }

    public class QuoteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_bar_author_image_item_quote)
        ProgressBar progressBarLoadingAuthorImage;
        @BindView(R.id.image_author_shared)
        ImageView imageAuthor;
        @BindView(R.id.text_author_name_item_quote)
        TextView textAuthorName;
        @BindView(R.id.text_quote_date_item_quote)
        TextView textDateQuote;
        @BindView(R.id.text_caption_quote_item_quote)
        TextView textCaptionQuote;
        @BindView(R.id.progress_bar_quote_item_quote)
        ProgressBar progressBarLoadingQuoteImage;
        @BindView(R.id.image_quote_shared)
        ImageView imageQuote;
        @BindView(R.id.text_total_likes_item_quote)
        TextView textTotalLikes;
        @BindView(R.id.text_total_comments_item_quote)
        TextView textTotalComments;
        @BindView(R.id.image_like_item_quote)
        ImageView imageLikeQuote;
        @BindView(R.id.image_comment_item_quote)
        ImageView imageCommentQuote;
        @BindView(R.id.image_share_item_quote)
        ImageView imageShareQuote;
        @BindView(R.id.image_more_options_item_quote)
        ImageView imageMoreOptions;
        @BindView(R.id.layout_comment_item_quote)
        LinearLayout layoutComment;


        public QuoteViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Quote quote, final onQuoteItemClickListener listener) {

            RequestOptions quoteImageOptions = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.quote_placeholder)
                    .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                    .centerCrop();

            Glide.with(mContext)
                    .load(quote.getImageUrl())
                    .apply(quoteImageOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBarLoadingQuoteImage.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBarLoadingQuoteImage.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageQuote);

            RequestOptions authorImageOptions = new RequestOptions()
                    .fitCenter()
                    .error(Utils.getInstance(mContext).getDrawable(R.drawable.ic_gallery_grey_24dp))
                    .circleCrop();

            Glide.with(mContext)
                    .load(quote.getAuthor().getProfileImage())
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


            textAuthorName.setText(quote.getAuthor().getName());
            textDateQuote.setText(quote.getDateAdded());
            textCaptionQuote.setText(quote.getCaption());
            if (!quote.getCaption().equalsIgnoreCase("") && !quote.getCaption().equalsIgnoreCase(null)) {
                textCaptionQuote.setVisibility(View.VISIBLE);
            } else {
                textCaptionQuote.setVisibility(View.GONE);
            }
            textTotalLikes.setText(quote.getTotalLikes());
            textTotalComments.setText(quote.getTotalComments());

            if (quote.isLikeQuote()) {
                imageLikeQuote.setBackgroundResource(R.drawable.ic_favorite_accent_24dp);
            } else {
                imageLikeQuote.setBackgroundResource(R.drawable.ic_favorite_border_accent_24dp);
            }

            imageAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAuthorClick(quote);
                }
            });

            textAuthorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAuthorClick(quote);
                }
            });

            textTotalLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTotalLikesClick(quote);
                }
            });

            imageLikeQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int dest = 0;
                    if (quote.isLikeQuote()) {
                        dest = -360;// rotate anti-clockwise

                    } else {
                        dest = 360;// rotate clockwise
                    }

                    if (imageLikeQuote.getRotation() == 360 || imageLikeQuote.getRotation() == -360) {
                        dest = 0;
                    }
                    mObjectAnimator = ObjectAnimator.ofFloat(imageLikeQuote,
                            "rotation", dest);
                    mObjectAnimator.setDuration(Constants.ANIMATION_CYCLE_DURATION);
                    mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
                    mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);

                    mObjectAnimator.start();
                    imageLikeQuote.setClickable(false);

                    listener.onActionLikeClick(quote, new onQuoteLikedListener() {
                        @Override
                        public void onQuoteLiked() {
                            imageLikeQuote.setClickable(true);
                            if (quote.isLikeQuote()) {
                                imageLikeQuote.setBackgroundResource(R.drawable.ic_favorite_border_accent_24dp);
                                mObjectAnimator.end();
                                quote.setLikeQuote(false);
                                textTotalLikes.setText(Integer.toString(Integer.parseInt(quote.getTotalLikes()) - 1));
                                quote.setTotalLikes(Integer.toString(Integer.parseInt(quote.getTotalLikes()) - 1));
                            } else {
                                imageLikeQuote.setBackgroundResource(R.drawable.ic_favorite_accent_24dp);
                                mObjectAnimator.end();
                                quote.setLikeQuote(true);
                                textTotalLikes.setText(Integer.toString(Integer.parseInt(quote.getTotalLikes()) + 1));
                                quote.setTotalLikes(Integer.toString(Integer.parseInt(quote.getTotalLikes()) + 1));
                            }
                        }
                    });
                }
            });

            layoutComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onActionCommentClick(quote);
                }
            });

            imageShareQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onActionShareClick(quote);
                }
            });

            imageMoreOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenuPopup(v, quote);
                }
            });

            imageQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onQuoteClick(quote, imageQuote, imageAuthor);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(quote);
                }
            });

        }
    }
}
