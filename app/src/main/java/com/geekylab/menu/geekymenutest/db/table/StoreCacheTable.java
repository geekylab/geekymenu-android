package com.geekylab.menu.geekymenutest.db.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.geekylab.menu.geekymenutest.db.MySQLiteOpenHelper;
import com.geekylab.menu.geekymenutest.db.entity.StoreEntity;
import com.geekylab.menu.geekymenutest.db.entity.StoreImageEntity;

import java.util.ArrayList;

/**
 * Created by johna on 16/12/14.
 * Kodokux System
 */
public class StoreCacheTable {

    public static final String TABLE_NAME = "store_cache";
    public static final String COL_ID = "_id";
    public static final String COL_STORE_NAME = "store_name";
    public static final String COL_TEL = "tel";
    public static final String COL_ADDRESS1 = "address1";
    public static final String COL_START_OPENING_HOUR = "start_opening_hour";
    public static final String COL_END_OPENING_HOUR = "end_opening_hour";
    public static final String COL_LAST_ORDER_TIME = "last_order_time";
    public static final String COL_DESCRIPTION = "description";
    private static StoreCacheTable sSingleton;
    private final Context context;
    private final MySQLiteOpenHelper mDBHelper;

    public static synchronized StoreCacheTable getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new StoreCacheTable(context);
        }
        return sSingleton;
    }

    public StoreCacheTable(Context c) {
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

    public long save(StoreEntity entity) {

        SQLiteDatabase writableDatabase = mDBHelper.getWritableDatabase();
        String sql = "SELECT `" + COL_ID + "` FROM " +
                TABLE_NAME +
                " WHERE `" + COL_ID + "` = ?";

        Cursor cursor = writableDatabase.rawQuery(sql, new String[]{entity.getId()});

        ContentValues values = new ContentValues();
        long retNum;
        values.put(COL_STORE_NAME, entity.getStoreName());
        if (entity.getTel() != null)
            values.put(COL_TEL, entity.getTel());
        if (entity.getAddress1() != null)
            values.put(COL_ADDRESS1, entity.getAddress1());
        if (entity.getStartOpeningHour() != null)
            values.put(COL_START_OPENING_HOUR, entity.getStartOpeningHour());
        if (entity.getEndOpeningHour() != null)
            values.put(COL_END_OPENING_HOUR, entity.getEndOpeningHour());
        if (entity.getLastOrderTime() != null)
            values.put(COL_LAST_ORDER_TIME, entity.getLastOrderTime());
        if (entity.getDescription() != null)
            values.put(COL_DESCRIPTION, entity.getDescription());
        if (cursor.getCount() == 0) {
            //insert
            values.put(COL_ID, entity.getId());
            retNum = writableDatabase.insert(TABLE_NAME, null, values);
        } else {
            //update
            retNum = writableDatabase.update(TABLE_NAME, values, "_id = ?", new String[]{entity.getId()});
        }

        if (retNum != -1) {
            StoreImagesCacheTable imagesCacheTable = StoreImagesCacheTable.getInstance(context);
            imagesCacheTable.removeAllByStoreId(entity.getId()); // remove all first
            ArrayList<StoreImageEntity> images = entity.getImages();
            if (images != null && images.size() > 0) {
                for (StoreImageEntity img : images) {
                    imagesCacheTable.save(img);
                }
            }
        }

        return retNum;
    }
}
