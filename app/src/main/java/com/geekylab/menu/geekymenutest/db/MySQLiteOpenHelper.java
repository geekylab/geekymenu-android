package com.geekylab.menu.geekymenutest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    static final String DB = "geekymenu.db";
    static final int DB_VERSION = 3;
    private static final String TAG = "MySQLiteOpenHelper";

    public MySQLiteOpenHelper(Context c) {
        super(c, DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(getGlobalCategoryTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropGlobalCategoryTable());
        onCreate(db);
    }

    private String getGlobalCategoryTableSql() {
        return "create table global_category (" +
                "_id INTEGER primary key autoincrement, " +
                "category_name TEXT not null, " +
                "image_data BLOB " +
                ");";
    }

    private String dropGlobalCategoryTable() {
        return "drop table global_category;";
    }
}
