/*
 * Copyright (c) 2017. Alfanse Developers
 *  All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 */

package com.alfanse.author.Models;

import android.graphics.Typeface;

/**
 * Created by Velocity-1601 on 5/10/2017.
 */

public class Font {

    private String fontName;
    private String fontFileName;
    private Typeface fontTypeface;

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontFileName() {
        return fontFileName;
    }

    public void setFontFileName(String fontFileName) {
        this.fontFileName = fontFileName;
    }

    public Typeface getFontTypeface() {
        return fontTypeface;
    }

    public void setFontTypeface(Typeface fontTypeface) {
        this.fontTypeface = fontTypeface;
    }
}
