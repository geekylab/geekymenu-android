package com.geekylab.menu.geekymenutest.db.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class ItemImageEntity implements Serializable {
    protected String id;
    protected String name;
    protected int image_type;
    private String url;

    public String getId() {
        return id;
    }

    public ItemImageEntity setId(String val) {
        this.id = val;
        return this;
    }

    public String getName() {
        return name;
    }

    public ItemImageEntity setName(String val) {
        this.name = val;
        return this;
    }

    public int getImageType() {
        return image_type;
    }

    public ItemImageEntity setImageType(int val) {
        this.image_type = val;
        return this;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
