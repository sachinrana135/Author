package com.alfanse.author.Models;

import java.io.Serializable;

/**
 * Created by Velocity-1601 on 6/29/2017.
 */

public class CommentFilters implements Serializable {
    private String quoteID;
    private String page;

    public String getQuoteID() {
        return quoteID;
    }

    public void setQuoteID(String quoteID) {
        this.quoteID = quoteID;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}