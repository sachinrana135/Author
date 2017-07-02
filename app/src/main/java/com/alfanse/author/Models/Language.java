package com.alfanse.author.Models;

import java.io.Serializable;

/**
 * Created by Velocity-1601 on 6/24/2017.
 */

public class Language implements Serializable {

    private String languageId;
    private String languageName;

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}
