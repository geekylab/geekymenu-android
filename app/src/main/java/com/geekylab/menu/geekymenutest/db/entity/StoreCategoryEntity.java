package com.geekylab.menu.geekymenutest.db.entity;

import java.io.Serializable;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class StoreCategoryEntity implements Serializable {
    protected String id;
    protected String name;
    protected String imageUrl;

    public String getId() {
        return id;
    }

    public StoreCategoryEntity setId(String val) {
        this.id = val;
        return this;
    }

    public String getName() {
        return name;
    }

    public StoreCategoryEntity setName(String val) {
        this.name = val;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public StoreCategoryEntity setImageUrl(String val) {
        this.imageUrl = val;
        return this;
    }

}
