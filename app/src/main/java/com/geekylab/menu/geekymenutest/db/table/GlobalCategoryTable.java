package com.geekylab.menu.geekymenutest.db.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.geekylab.menu.geekymenutest.db.MySQLiteOpenHelper;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class GlobalCategoryTable extends MySQLiteOpenHelper {
    protected String TABLE_NAME = "global_category";
    public static final String COL_ID = "_id";
    public static final String COL_CATEGORY_NAME = "category_name";
    public static final String COL_IMAGE_DATA = "image_data";

    public GlobalCategoryTable(Context c) {
        super(c);
    }

    public Long insert(ContentValues values) {
        return getWritableDatabase()
                .insert(TABLE_NAME, null, values);
    }

    public Cursor findAll() {
        return getReadableDatabase().query(
                "global_category",
                new String[]{"_id", "category_name", "category_name"},
                null,
                null,
                null,
                null,
                "_id DESC"
        );
    }

}
