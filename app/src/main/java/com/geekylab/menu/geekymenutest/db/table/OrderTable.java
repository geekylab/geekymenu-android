package com.geekylab.menu.geekymenutest.db.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.geekylab.menu.geekymenutest.db.MySQLiteOpenHelper;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class OrderTable extends MySQLiteOpenHelper {
    protected String TABLE_NAME = "user_order";
    public static final String COL_ID = "_id";
    public static final String COL_STORE_ID = "store_id";
    public static final String COL_ORDER_TOKEN = "order_token";
    public static final String COL_TABLE_TOKEN = "table_token";
    public static final String COL_STATUS = "status";
    public static final String COL_CREATED_AT = "created_at";

    public OrderTable(Context c) {
        super(c);
    }

    public Long insert(ContentValues values) {
        return getWritableDatabase()
                .insert(TABLE_NAME, null, values);
    }

    public Cursor findByStoreId(String store_id) {
        return getReadableDatabase().query(
                TABLE_NAME,
                new String[]{
                        COL_ID,
                        COL_STORE_ID,
                        COL_TABLE_TOKEN,
                        COL_STATUS,
                        COL_CREATED_AT
                },
                COL_STORE_ID + "='" + store_id + "'",
                null,
                null,
                null,
                COL_ID + " DESC"
        );
    }


}
