package com.geekylab.menu.geekymenutest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.geekylab.menu.geekymenutest.db.table.OrderTable;
import com.geekylab.menu.geekymenutest.db.table.StoreCacheTable;
import com.geekylab.menu.geekymenutest.db.table.StoreImagesCacheTable;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static MySQLiteOpenHelper sSingleton = null;
    public static final String DB = "geekymenu1.db";
    public static final int DB_VERSION = 6;
    private static final String TAG = "MySQLiteOpenHelper";

    public static synchronized MySQLiteOpenHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new MySQLiteOpenHelper(context);
        }
        return sSingleton;
    }

    public MySQLiteOpenHelper(Context c) {
        super(c, DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getGlobalCategoryTableSql());
        db.execSQL(getOrderTableSql());
        db.execSQL(getOrderItemTableSql());
        db.execSQL(getStoreCacheTableSql());
        db.execSQL(getStoreImagesCacheTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropGlobalCategoryTable());
        db.execSQL(dropOrderTableSql());
        db.execSQL(dropOrderItemTableSql());
        db.execSQL(dropStoreCacheTable());
        db.execSQL(dropStoreImagesCacheTable());
        onCreate(db);
    }

    private String getGlobalCategoryTableSql() {
        return "create table global_category (" +
                "_id INTEGER primary key autoincrement, " +
                "category_name TEXT not null, " +
                "image_data BLOB " +
                ");";
    }

    private String getOrderTableSql() {
        return "create table user_order (" +
                OrderTable.COL_ID + " INTEGER primary key autoincrement, " +
                OrderTable.COL_STORE_ID + " TEXT not null, " +
                OrderTable.COL_ORDER_TOKEN + " TEXT not null, " +
                OrderTable.COL_ORDER_NUMBER + " TEXT not null, " +
                OrderTable.COL_TABLE_TOKEN + " INTEGER DEFAULT 0, " +
                OrderTable.COL_STATUS + " INTEGER DEFAULT 0, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP " +
                ");";
    }

    private String getOrderItemTableSql() {
        return "create table user_order_item (" +
                "_id INTEGER primary key, " +
                "order_id INTEGER, " +
                "name TEXT not null, " +
                "status INTEGER DEFAULT 0, " +
                "price DECIMAL(10,5) DEFAULT 0, " +
                "time INTEGER DEFAULT 0, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP " +
                ");";
    }

    private String getStoreCacheTableSql() {
        return "create table " + StoreCacheTable.TABLE_NAME + " (" +
                StoreCacheTable.COL_ID + " TEXT primary key, " +
                StoreCacheTable.COL_STORE_NAME + " TEXT not null, " +
                StoreCacheTable.COL_DESCRIPTION + " TEXT, " +
                StoreCacheTable.COL_TEL + " TEXT, " +
                StoreCacheTable.COL_ADDRESS1 + " TEXT, " +
                StoreCacheTable.COL_START_OPENING_HOUR + " TEXT, " +
                StoreCacheTable.COL_END_OPENING_HOUR + " TEXT, " +
                StoreCacheTable.COL_LAST_ORDER_TIME + " TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP " +
                ");";
    }

    private String getStoreImagesCacheTableSql() {
        return "create table " + StoreImagesCacheTable.TABLE_NAME + " (" +
                StoreImagesCacheTable.COL_ID + " TEXT primary key, " +
                StoreImagesCacheTable.COL_STORE_ID + " TEXT, " +
                StoreImagesCacheTable.COL_IMAGE_URL + " TEXT not null, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP " +
                ");"
                //index
                + "create index storeIdIdx on " + StoreImagesCacheTable.TABLE_NAME + "(" + StoreImagesCacheTable.COL_STORE_ID + ")";
    }

    private String dropOrderTableSql() {
        return "DROP TABLE IF EXISTS user_order;";
    }

    private String dropOrderItemTableSql() {
        return "DROP TABLE IF EXISTS user_order_item;";
    }

    private String dropGlobalCategoryTable() {
        return "DROP TABLE IF EXISTS global_category;";
    }

    private String dropStoreCacheTable() {
        return "DROP TABLE IF EXISTS " + StoreCacheTable.TABLE_NAME + ";";
    }

    private String dropStoreImagesCacheTable() {
        return "DROP TABLE IF EXISTS " + StoreImagesCacheTable.TABLE_NAME + ";";
    }
}
