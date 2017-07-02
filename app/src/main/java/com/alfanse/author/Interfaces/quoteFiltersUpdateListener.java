package com.alfanse.author.Interfaces;

import com.alfanse.author.Models.QuoteFilters;

/**
 * Created by Velocity-1601 on 6/29/2017.
 */

public interface quoteFiltersUpdateListener {
    void onQuoteFiltersUpdate(QuoteFilters quoteFilters);

    void closeQuoteFiltersFragment();
}
