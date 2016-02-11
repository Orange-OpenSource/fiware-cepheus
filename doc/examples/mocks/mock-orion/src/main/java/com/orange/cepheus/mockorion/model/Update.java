/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.mockorion.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by pborscia on 21/08/2015.
 */
public class Update {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String type;

    @NotNull
    private Boolean isPattern;

    @NotNull
    @NotEmpty
    private String attributName;

    @NotNull
    @NotEmpty
    private String attributType;

    @NotNull
    @NotEmpty
    private String attributValue;

    public Update() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsPattern() {
        return isPattern;
    }

    public void setIsPattern(Boolean isPattern) {
        this.isPattern = isPattern;
    }

    public String getAttributName() {
        return attributName;
    }

    public void setAttributName(String attributName) {
        this.attributName = attributName;
    }

    public String getAttributType() {
        return attributType;
    }

    public void setAttributType(String attributType) {
        this.attributType = attributType;
    }

    public String getAttributValue() {
        return attributValue;
    }

    public void setAttributValue(String attributValue) {
        this.attributValue = attributValue;
    }

    @Override
    public String toString() {
        return "Update{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isPattern=" + isPattern +
                ", attributName='" + attributName + '\'' +
                ", attributType='" + attributType + '\'' +
                ", attributValue='" + attributValue + '\'' +
                '}';
    }
}
