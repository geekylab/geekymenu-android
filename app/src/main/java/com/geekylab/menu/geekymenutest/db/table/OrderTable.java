package com.geekylab.menu.geekymenutest.db.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geekylab.menu.geekymenutest.db.MySQLiteOpenHelper;
import com.geekylab.menu.geekymenutest.db.entity.UserOrderEntity;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class OrderTable {
    private static final String TAG = OrderTable.class.getSimpleName();
    //[{"status":0,"order":{"__v":0,"order_token":"c0dc2bcf36800c5d75fdb49e8bebdb89eb9fb1d2","status":1,"table":"54946933f770d62d0075057e","order_number":14,"_id":"549497a41eda58090243eafb","created":"2014-12-19T21:24:52.236Z","request_count":1,"orderItems":[],"customers":["54946952bc39285e001b3a6e"]}}]
    private static OrderTable sSingleton;
    private final MySQLiteOpenHelper mDBHelper;
    protected String TABLE_NAME = "user_order";
    public static final int ORDER_STATUS_ACTIVE = 1;
    public static final int ORDER_STATUS_CLOSE = 2;
    public static final String COL_ID = "_id";
    public static final String COL_STORE_ID = "store_id";
    public static final String COL_TABLE_ID = "table_id";
    public static final String COL_ORDER_TOKEN = "order_token";
    public static final String COL_ORDER_NUMBER = "order_number";
    public static final String COL_CUSTOMER_ID = "customer";
    public static final String COL_TOTAL_PRICE = "total_price";
    public static final String COL_STATUS = "status";
    public static final String COL_CREATED_AT = "created_at";

    public static synchronized OrderTable getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new OrderTable(context);
        }
        return sSingleton;
    }

    public OrderTable(Context c) {
        mDBHelper = MySQLiteOpenHelper.getInstance(c);
    }

    public Long insert(ContentValues values) {
        return mDBHelper.getWritableDatabase()
                .insert(TABLE_NAME, null, values);
    }

    public Cursor findByStoreId(String store_id) {
        return mDBHelper.getReadableDatabase().query(
                TABLE_NAME,
                new String[]{
                        COL_ID,
                        COL_STORE_ID,
                        COL_ORDER_NUMBER,
                        COL_ORDER_TOKEN,
                        COL_TABLE_ID,
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


    public long save(UserOrderEntity orderEntity) {
        if (orderEntity.getId() != null) {
            SQLiteDatabase writableDatabase = mDBHelper.getWritableDatabase();
            String sql = "SELECT `" + COL_ID + "` FROM " +
                    TABLE_NAME +
                    " WHERE `" + COL_ID + "` = ?";

            Cursor cursor = writableDatabase.rawQuery(sql, new String[]{orderEntity.getId()});
            ContentValues values = new ContentValues();
            long retNum;

            values.put(COL_STORE_ID, orderEntity.getStoreId());
            values.put(COL_TABLE_ID, orderEntity.getTable());
            values.put(COL_ORDER_TOKEN, orderEntity.getOrderToken());
            values.put(COL_ORDER_NUMBER, orderEntity.getOrderNumber());
            values.put(COL_CUSTOMER_ID, orderEntity.getCustomer());
            values.put(COL_STATUS, orderEntity.getStatus());

            if (cursor.getCount() == 0) {
                //insert
                values.put(COL_ID, orderEntity.getId());
                retNum = writableDatabase.insert(TABLE_NAME, null, values);
                Log.d(TAG, "insert order");
            } else {
                //update
                retNum = writableDatabase.update(TABLE_NAME, values, "_id = ?", new String[]{orderEntity.getId()});
                Log.d(TAG, "update order");
            }
            return retNum;
        }

        return -1;
    }
}
