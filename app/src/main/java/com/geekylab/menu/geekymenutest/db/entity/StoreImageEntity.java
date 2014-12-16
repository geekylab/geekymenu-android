package com.geekylab.menu.geekymenutest.db.entity;

import java.io.Serializable;

/**
 * Created by johna on 16/12/14.
 * Kodokux System
 */
public class StoreImageEntity implements Serializable {
    private String id;
    private String store_id;
    private String image_url;

    public String getId() {
        return id;
    }

    public StoreImageEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getStoreId() {
        return store_id;
    }

    public StoreImageEntity setStoreId(String store_id) {
        this.store_id = store_id;
        return this;
    }

    public String getImageUrl() {
        return image_url;
    }

    public StoreImageEntity setImageUrl(String image_url) {
        this.image_url = image_url;
        return this;
    }
}
