package com.geekylab.menu.geekymenutest.db.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.geekylab.menu.geekymenutest.db.MySQLiteOpenHelper;
import com.geekylab.menu.geekymenutest.db.entity.StoreEntity;

/**
 * Created by johna on 16/12/14.
 * Kodokux System
 */
public class StoreCacheTable extends MySQLiteOpenHelper {

    public static final String TABLE_NAME = "store_cache";
    public static final String COL_ID = "_id";
    public static final String COL_STORE_NAME = "store_name";
    public static final String COL_TEL = "tel";
    public static final String COL_ADDRESS1 = "address1";
    public static final String COL_START_OPENING_HOUR = "start_opening_hour";
    public static final String COL_END_OPENING_HOUR = "end_opening_hour";
    public static final String COL_LAST_ORDER_TIME = "last_order_time";
    public static final String COL_DESCRIPTION = "description";


    public StoreCacheTable(Context c) {
        super(c);
    }

    public long save(StoreEntity entity) {

        SQLiteDatabase writableDatabase = getWritableDatabase();
        String sql = "SELECT `" + COL_ID + "` FROM " +
                TABLE_NAME +
                " WHERE `" + COL_ID + "` = ?";

        Cursor cursor = writableDatabase.rawQuery(sql, new String[]{entity.getId()});

        ContentValues values = new ContentValues();
        values.put(COL_ID, entity.getId());
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
            return writableDatabase.insert(TABLE_NAME, null, values);
        } else {
            //update
            return writableDatabase.update(TABLE_NAME, values, "_id = " + entity.getId(), null);
        }
    }
}
