package com.geekylab.menu.geekymenutest.db.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class StoreEntity implements Serializable {
    protected String id;
    protected String store_name;
    private int seatCount;
    private ArrayList<String> images;
    private String tel;
    private String address1;
    private String startOpeningHour;
    private String endOpeningHour;
    private String lastOrderTime;
    private String description;

    public String getId() {
        return id;
    }

    public StoreEntity setId(String val) {
        this.id = val;
        return this;
    }

    public String getStoreName() {
        return store_name;
    }

    public StoreEntity setStoreName(String val) {
        store_name = val;
        return this;
    }

    public StoreEntity setSeatCount(int seatCount) {
        this.seatCount = seatCount;
        return this;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public StoreEntity setImages(ArrayList<String> images) {
        this.images = images;
        return this;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public StoreEntity setTel(String tel) {
        this.tel = tel;
        return this;
    }

    public String getTel() {
        return tel;
    }

    public StoreEntity setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public String getAddress1() {
        return address1;
    }

    public StoreEntity setStartOpeningHour(String startOpeningHour) {
        this.startOpeningHour = startOpeningHour;
        return this;
    }

    public String getStartOpeningHour() {
        return startOpeningHour;
    }

    public StoreEntity setEndOpeningHour(String endOpeningHour) {
        this.endOpeningHour = endOpeningHour;
        return this;
    }

    public String getEndOpeningHour() {
        return endOpeningHour;
    }

    public StoreEntity setLastOrderTime(String lastOrderTime) {
        this.lastOrderTime = lastOrderTime;
        return this;
    }

    public String getLastOrderTime() {
        return lastOrderTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
