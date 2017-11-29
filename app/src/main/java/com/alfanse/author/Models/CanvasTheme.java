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

import com.alfanse.author.Utilities.Constants;

/**
 * Created by Velocity-1601 on 4/28/2017.
 */

public class CanvasTheme {

    private String id;
    private String imageUrl;
    private String textSize;
    private String textFontFamily;
    private String textColor;
    private String textStyle;
    private String textLocationX; // value in percentage
    private String textLocationY; // value in percentage

    public CanvasTheme() {
    }

    public static CanvasTheme getDefaultTheme() {

        CanvasTheme canvasTheme = new CanvasTheme();

        canvasTheme.setTextFontFamily(Constants.DEFAULT_COMPONENT_TEXTVIEW_FONT);
        canvasTheme.setTextStyle(Constants.DEFAULT_COMPONENT_TEXTVIEW_STYLE);
        canvasTheme.setTextColor(Constants.DEFAULT_COMPONENT_TEXTVIEW_COLOR);
        canvasTheme.setTextSize(Constants.DEFAULT_COMPONENT_TEXTVIEW_SIZE);
        canvasTheme.setTextStyle(Constants.DEFAULT_COMPONENT_TEXTVIEW_STYLE);
        canvasTheme.setTextLocationX(Constants.DEFAULT_COMPONENT_TEXTVIEW_LOCATION_X);
        canvasTheme.setTextLocationY(Constants.DEFAULT_COMPONENT_TEXTVIEW_LOCATION_Y);
        return canvasTheme;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTextSize() {
        return textSize;
    }

    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    public String getTextFontFamily() {
        return textFontFamily;
    }

    public void setTextFontFamily(String textFontFamily) {
        this.textFontFamily = textFontFamily;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(String textStyle) {
        this.textStyle = textStyle;
    }

    public String getTextLocationX() {
        return textLocationX;
    }

    public void setTextLocationX(String textLocationX) {
        this.textLocationX = textLocationX;
    }

    public String getTextLocationY() {
        return textLocationY;
    }

    public void setTextLocationY(String textLocationY) {
        this.textLocationY = textLocationY;
    }
}
