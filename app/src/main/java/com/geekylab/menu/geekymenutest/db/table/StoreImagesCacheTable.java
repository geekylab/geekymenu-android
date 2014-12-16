package com.geekylab.menu.geekymenutest.db.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.geekylab.menu.geekymenutest.db.MySQLiteOpenHelper;
import com.geekylab.menu.geekymenutest.db.entity.StoreEntity;
import com.geekylab.menu.geekymenutest.db.entity.StoreImageEntity;

/**
 * Created by johna on 16/12/14.
 * Kodokux System
 */
public class StoreImagesCacheTable {

    public static final String TABLE_NAME = "store_images_cache";
    public static final String COL_ID = "_id";
    public static final String COL_STORE_ID = "store_id";
    public static final String COL_IMAGE_URL = "image_url";
    private static StoreImagesCacheTable sSingleton;
    private final Context context;
    private final MySQLiteOpenHelper mDBHelper;

    public static synchronized StoreImagesCacheTable getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new StoreImagesCacheTable(context);
        }
        return sSingleton;
    }

    public StoreImagesCacheTable(Context c) {
        context = c;
        mDBHelper = MySQLiteOpenHelper.getInstance(c);
    }

    public Cursor findById(String id) {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        String sql = "SELECT * FROM " +
                TABLE_NAME +
                " WHERE `" + COL_ID + "` = ?";
        return database.rawQuery(sql, new String[]{id});
    }

    public Cursor findByStoreId(String storeId) {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        String sql = "SELECT * FROM " +
                TABLE_NAME +
                " WHERE `" + COL_STORE_ID + "` = ?";
        return database.rawQuery(sql, new String[]{storeId});
    }

    public long save(StoreImageEntity entity) {

        SQLiteDatabase writableDatabase = mDBHelper.getWritableDatabase();
        String sql = "SELECT `" + COL_ID + "` FROM " +
                TABLE_NAME +
                " WHERE `" + COL_ID + "` = ?";

        Cursor cursor = writableDatabase.rawQuery(sql, new String[]{entity.getId()});

        ContentValues values = new ContentValues();
        values.put(COL_STORE_ID, entity.getStoreId());
        if (entity.getImageUrl() != null)
            values.put(COL_IMAGE_URL, entity.getImageUrl());
        if (cursor.getCount() == 0) {
            //insert
            values.put(COL_ID, entity.getId());
            return writableDatabase.insert(TABLE_NAME, null, values);
        } else {
            //update
            return writableDatabase.update(TABLE_NAME, values, "_id = " + entity.getId(), null);
        }
    }

    public long removeAllByStoreId(String id) {
        return mDBHelper.getWritableDatabase().delete(TABLE_NAME, COL_STORE_ID + " = ?", new String[]{id});
    }
}
