package com.geekylab.menu.geekymenutest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.geekylab.menu.geekymenutest.db.table.OrderTable;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String DB = "geekymenu1.db";
    public static final int DB_VERSION = 3;
    private static final String TAG = "MySQLiteOpenHelper";

    public MySQLiteOpenHelper(Context c) {
        super(c, DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getGlobalCategoryTableSql());
        db.execSQL(getOrderTableSql());
        db.execSQL(getOrderItemTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropGlobalCategoryTable());
        db.execSQL(dropOrderTableSql());
        db.execSQL(dropOrderItemTableSql());
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

    private String dropOrderTableSql() {
        return "DROP TABLE IF EXISTS user_order;";
    }

    private String dropOrderItemTableSql() {
        return "DROP TABLE IF EXISTS user_order_item;";
    }

    private String dropGlobalCategoryTable() {
        return "DROP TABLE IF EXISTS global_category;";
    }
}
