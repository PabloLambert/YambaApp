package com.lambertsoft.yambaapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by InnovaTI on 03-01-15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = DBHelper.class.getName();

    public DBHelper (Context context) {
        super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s ( %s int primary key, %s text, %s text, %s int)",
                StatusContract.TABLE,
                StatusContract.Column.ID,
                StatusContract.Column.USER,
                StatusContract.Column.MESSAGE,
                StatusContract.Column.CREATED_AT);
        Log.d(TAG, "onCreate on SQL:" + sql);
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + StatusContract.TABLE);
        onCreate(db);
    }
}
