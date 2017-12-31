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

    void onActionDeleteClick(Quote quote);

    void onAuthorClick(Quote quote);
}
