/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Interfaces;

import android.widget.ImageView;

import com.alfanse.author.Models.Quote;

/**
 * Created by Velocity-1601 on 6/24/2017.
 */

public interface onQuoteItemClickListener {
    void onItemClick(Quote quote);

    void onQuoteClick(Quote quote, ImageView quoteImage, ImageView authorImage);

    void onActionLikeClick(Quote quote, onQuoteLikedListener listener);

    void onTotalLikesClick(Quote quote);

    void onActionCommentClick(Quote quote);

    void onActionShareClick(Quote quote);

    void onActionFollowClick(Quote quote, onAuthorFollowedListener listener);

    void onActionDownloadClick(Quote quote);

    void onActionReportClick(Quote quote);

    void onAuthorClick(Quote quote);
}
