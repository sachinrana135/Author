/*
 * Copyright (c) 2018. Alfanse Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.alfanse.author.Utilities.GPUFilters;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class GPUImageHalftoneFilter extends GPUImageFilter {
    public static final String HALFTONE_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +

            "uniform sampler2D inputImageTexture;\n" +

            "uniform highp float fractionalWidthOfPixel;\n" +
            "uniform highp float aspectRatio;\n" +

            "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +

            "void main()\n" +
            "{\n" +
            "  highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);\n" +
            "  highp vec2 samplePos = textureCoordinate - mod(textureCoordinate, sampleDivisor) + 0.5 * sampleDivisor;\n" +
            "  highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
            "  highp vec2 adjustedSamplePos = vec2(samplePos.x, (samplePos.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
            "  highp float distanceFromSamplePoint = distance(adjustedSamplePos, textureCoordinateToUse);\n" +
            "  lowp vec3 sampledColor = texture2D(inputImageTexture, samplePos).rgb;\n" +
            "  highp float dotScaling = 1.0 - dot(sampledColor, W);\n" +
            "  lowp float checkForPresenceWithinDot = 1.0 - step(distanceFromSamplePoint, (fractionalWidthOfPixel * 0.5) * dotScaling);\n" +
            "  gl_FragColor = vec4(vec3(checkForPresenceWithinDot), 1.0);\n" +
            "}";

    private int mFractionalWidthOfPixelLocation;
    private int mAspectRatioLocation;

    private float mFractionalWidthOfAPixel;
    private float mAspectRatio;

    public GPUImageHalftoneFilter() {
        this(0.01f);
    }

    public GPUImageHalftoneFilter(float fractionalWidthOfAPixel) {
        super(NO_FILTER_VERTEX_SHADER, HALFTONE_FRAGMENT_SHADER);
        mFractionalWidthOfAPixel = fractionalWidthOfAPixel;
    }

    @Override
    public void onInit() {
        super.onInit();
        mFractionalWidthOfPixelLocation = GLES20.glGetUniformLocation(getProgram(), "fractionalWidthOfPixel");
        mAspectRatioLocation = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");
        setFractionalWidthOfAPixel(mFractionalWidthOfAPixel);
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setAspectRatio((float) height / (float) width);
    }

    public void setFractionalWidthOfAPixel(final float fractionalWidthOfAPixel) {
        mFractionalWidthOfAPixel = fractionalWidthOfAPixel;
        setFloat(mFractionalWidthOfPixelLocation, mFractionalWidthOfAPixel);
    }

    public void setAspectRatio(final float aspectRatio) {
        mAspectRatio = aspectRatio;
        setFloat(mAspectRatioLocation, mAspectRatio);
    }
}
