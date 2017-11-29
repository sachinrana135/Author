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
