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

package com.alfanse.author.Models;

import com.alfanse.author.Utilities.GPUImageFilterTools;

/**
 * Created by Sachin on 4/17/2018.
 */

public class Filter {

    private String title;
    private GPUImageFilterTools.FilterType filter;
    private Boolean selected = false;

    public Filter(String title, GPUImageFilterTools.FilterType filter, Boolean selected) {
        this.title = title;
        this.filter = filter;
        this.selected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GPUImageFilterTools.FilterType getFilter() {
        return filter;
    }

    public void setFilter(GPUImageFilterTools.FilterType filter) {
        this.filter = filter;
    }

    public Boolean isSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
