package com.geekylab.menu.geekymenutest.db.entity;

import java.io.Serializable;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class GlobalCategoryEntity implements Serializable {
    protected int id;
    protected String category_name;
    protected String category_image_url;

    public int getId() {
        return id;
    }

    public GlobalCategoryEntity setId(int val) {
        this.id = val;
        return this;
    }

    public String getName() {
        return category_name;
    }

    public GlobalCategoryEntity setName(String val) {
        category_name = val;
        return this;
    }

    public String getImageUrl() {
        return category_image_url;
    }

    public GlobalCategoryEntity setImageUrl(String val) {
        category_image_url = val;
        return this;
    }


//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(getName());
//        dest.writeString(getImageUrl());
//    }
//
//    public static final Parcelable.Creator<GlobalCategoryEntity> CREATOR
//            = new Parcelable.Creator<GlobalCategoryEntity>() {
//        public GlobalCategoryEntity createFromParcel(Parcel in) {
//            GlobalCategoryEntity globalCategoryEntity = new GlobalCategoryEntity();
//            globalCategoryEntity.setName(in.readString());
//            globalCategoryEntity.setImageUrl(in.readString());
//            return globalCategoryEntity;
//        }
//
//        public GlobalCategoryEntity[] newArray(int size) {
//            return new GlobalCategoryEntity[size];
//        }
//    };
}
