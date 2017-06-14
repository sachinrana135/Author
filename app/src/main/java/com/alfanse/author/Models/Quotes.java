package com.alfanse.author.Models;

import java.io.Serializable;

/**
 * Created by Velocity-1601 on 6/14/2017.
 */

class Quotes implements Serializable {

    private Quote[] quotes;

    public Quote[] getQuotes() {
        return quotes;
    }

    public void setQuotes(Quote[] quotes) {
        this.quotes = quotes;
    }
}
