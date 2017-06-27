package com.alfanse.author.Interfaces;

import com.alfanse.author.Models.Quote;

/**
 * Created by Velocity-1601 on 6/24/2017.
 */

public interface onQuoteItemClickListener {
    void onItemClick(Quote quote);

    void onQuoteClick(Quote quote);

    void onActionLikeClick(Quote quote);

    void onTotalLikesClick(Quote quote);

    void onActionCommentClick(Quote quote);

    void onActionShareClick(Quote quote);

    void onActionFollowClick(Quote quote);

    void onActionDownloadClick(Quote quote);

    void onActionReportClick(Quote quote);

    void onAuthorClick(Quote quote);
}
