package com.geekylab.menu.geekymenutest.db.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class ItemEntity implements Serializable {
    protected String id;
    protected String name;
    protected String desc;
    protected String price;
    protected String time;
    private ArrayList<ItemImageEntity> imageUrls;

    public String getId() {
        return id;
    }

    public ItemEntity setId(String val) {
        this.id = val;
        return this;
    }

    public String getName() {
        return name;
    }

    public ItemEntity setName(String val) {
        this.name = val;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public ItemEntity setDesc(String val) {
        this.desc = val;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public ItemEntity setPrice(String val) {
        this.price = val;
        return this;
    }

    public String getTime() {
        return time;
    }

    public ItemEntity setTime(String val) {
        this.time = val;
        return this;
    }

    public void setImageUrls(ArrayList<ItemImageEntity> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public ArrayList<ItemImageEntity> getImageUrls() {
        return imageUrls;
    }
}
