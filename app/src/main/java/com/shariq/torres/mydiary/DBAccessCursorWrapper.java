package com.shariq.torres.mydiary;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import java.util.Date;

/**
 * Created by Torres on 4/2/2016.
 */
public class DBAccessCursorWrapper extends CursorWrapper {
    public DBAccessCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Entry getEntry(){
        int id = getInt(getColumnIndex("_id"));
        String title = getString(getColumnIndex("title"));
        String text = getString(getColumnIndex("text"));
        Date created = new Date(getLong(getColumnIndex("created")));
        boolean hasPhotos;
        if(isNull(getColumnIndex("filename"))){
            hasPhotos = false;
        }else{
            hasPhotos = true;
        }
        
        Entry newEntry = new Entry(title, text, hasPhotos);
        newEntry.setId(id);
        Log.d("API", "This is id of the entry that was created: " + String.valueOf(newEntry.getId()));
        newEntry.setEntryDate( created);
        return newEntry;


    }
}
