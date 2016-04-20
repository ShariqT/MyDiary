package com.shariq.torres.mydiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Torres on 4/2/2016.
 */
public class DBAccess extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydiary.db";
    private static final int VERSION = 1;

    public DBAccess(Context context){
        super(context, DB_NAME, null, VERSION);
        //context.deleteDatabase(DB_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS entries ('_id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'title' TEXT NOT NULL, 'text' TEXT NOT NULL, 'created' BIGINT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS photos ('_id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'post_id' INTEGER NOT NULL, 'filename' TEXT NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
