package com.alfanse.author.Models;

import java.io.Serializable;

/**
 * Created by Velocity-1601 on 6/29/2017.
 */

public class AuthorFilters implements Serializable {
    private String filterType;
    private String authorID;
    private String loggedAuthorID;
    private String quoteID;
    private String page;

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getLoggedAuthorID() {
        return loggedAuthorID;
    }

    public void setLoggedAuthorID(String loggedAuthorID) {
        this.loggedAuthorID = loggedAuthorID;
    }

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
