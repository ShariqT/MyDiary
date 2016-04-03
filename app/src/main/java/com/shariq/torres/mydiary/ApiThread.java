package com.shariq.torres.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;


import java.util.ArrayList;

/**
 * Created by Torres on 3/14/2016.
 */
public class ApiThread extends HandlerThread {

    private Handler requestHandler;
    private Handler responseHandler;
    private ApiThreadListener onCompleteListener;
    private final int GET_ENTRIES = 0;
    private final int GET_PHOTOS = 1;
    private final int CREATE_ENTRY = 2;
    private final int SAVE_ENTRY = 3;
    private Context context;
    private SQLiteDatabase db;
    public ApiThread(Handler responseHandler, Context context){
        super("APIThread");
        this.responseHandler = responseHandler;
        this.context = context;

    }

    @Override
    protected void onLooperPrepared(){
        requestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Bundle myMsg;
                Entry entryToSave;
                switch(msg.what){
                    case GET_ENTRIES:
                        getListOfEntries();
                        break;
                    case GET_PHOTOS:
                        myMsg = msg.getData();
                        entryToSave = myMsg.getParcelable("entry");
                        returnEntryPhotos(entryToSave);
                        break;
                    case CREATE_ENTRY:
                        myMsg = msg.getData();
                        entryToSave = myMsg.getParcelable("entry");
                        returnEntry(entryToSave);
                        break;
                    case SAVE_ENTRY:
                        myMsg = msg.getData();
                        entryToSave = myMsg.getParcelable("entry");
                        saveEntryToDB(entryToSave);
                        break;
                }
            }
        };
    }

    public interface ApiThreadListener {
        void onCompleteArray(ArrayList<Entry> entryData);
        void onCompleteCode(long returnCode);
        void onCompleteEntry(Entry entry);

    }

    public void setOnCompleteListener(ApiThreadListener listener){
        onCompleteListener = listener;
    }

    public void getEntryList(){
        requestHandler.obtainMessage(GET_ENTRIES).sendToTarget();
    }

    public void getEntryPhotos(int entry_id){
        requestHandler.obtainMessage(GET_PHOTOS, entry_id, 0).sendToTarget();
    }

    public void createEntry(Entry entryToAdd){
        Bundle bundle = new Bundle();
        bundle.putParcelable("entry", entryToAdd);
        Message msg = requestHandler.obtainMessage(CREATE_ENTRY);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    public void saveEntry(Entry entryToSave){
        Bundle bundle = new Bundle();
        bundle.putParcelable("entry", entryToSave);
        Message msg = requestHandler.obtainMessage(SAVE_ENTRY);
        msg.setData(bundle);
        msg.sendToTarget();
    }



    private DBAccessCursorWrapper buildEntryQuery(){
        if(this.db == null){
            this.db = new DBAccess(this.context).getWritableDatabase();
        }
        Cursor cursor = this.db.rawQuery("SELECT  entries._id, entries.title, entries.text, entries.created, photos.filename FROM entries LEFT JOIN photos ON entries._id = photos.post_id group by entries._id order by entries._id;", null);
        return new DBAccessCursorWrapper(cursor);
    }

    private DBAccessCursorWrapper buildPhotoQuery(Entry entry){
        if(this.db == null){
            this.db = new DBAccess(this.context).getWritableDatabase();
        }

        Cursor cursor = this.db.rawQuery("SELECT filename FROM photos WHERE post_id = ?", new String[]{String.valueOf(entry.getId())});
        return new DBAccessCursorWrapper(cursor);
    }

    private long buildInsertQuery(Entry entry){

        if(this.db == null){
            this.db = new DBAccess(this.context).getWritableDatabase();
        }
        ContentValues values = setUpEntryForDB(entry);
        long t = this.db.insert("entries", null, values);
        Log.d("API Diary", "the new id for the insert is " + String.valueOf(t));
        if(entry.getPhotoList().size() > 1) {
            for (String src : entry.getPhotoList()) {
                ContentValues pvalues = setUpPhotosForDB(entry.getId(), src);
                this.db.insert("photos", null, pvalues);
            }
        }
        return 1;
    }


    private ContentValues setUpEntryForDB(Entry entry){
        ContentValues values = new ContentValues();
        Log.d("API Diary", "entry title is " + entry.getTitle());
        values.put("title", entry.getTitle());
        values.put("text", entry.getText());

        values.put("created", entry.getEntryDate().getTime());
        return values;
    }

    private ContentValues setUpPhotosForDB(int id, String src){
        ContentValues values = new ContentValues();
        values.put("post_id", id);
        values.put("filename", src);
        return values;
    }

    private long buildUpdateQuery(Entry entry){
        if(this.db == null){
            this.db = new DBAccess(this.context).getWritableDatabase();
        }
        ContentValues values = setUpEntryForDB(entry);
        Log.d("API Diary", "the item getting updated as an id of " + String.valueOf(entry.getId()));
        int t = this.db.update("entries", values, "_id = ?", new String[]{String.valueOf(entry.getId())});
        Log.d("API Diary", "for update, there was " + String.valueOf(t) + " rows affected");
        if(entry.getPhotoList().size() > 1) {
            this.db.delete("photos", "post_id = ?", new String[]{String.valueOf(entry.getId())});
            for (String src : entry.getPhotoList()) {
                ContentValues pvalues = setUpPhotosForDB(entry.getId(), src);
                this.db.insert("photos", null, pvalues);
            }
        }
        return 1;
    }




    protected void getListOfEntries(){
        final ArrayList<Entry> entryData = new ArrayList<Entry>();

        DBAccessCursorWrapper cursor = buildEntryQuery();
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                entryData.add(cursor.getEntry());
                cursor.moveToNext();
            }
        }finally{
            cursor.close();
        }

       //this.db.close();

        this.responseHandler.post(new Runnable() {
            @Override
            public void run() {
                onCompleteListener.onCompleteArray(entryData);
            }
        });
    }


    protected void returnEntryPhotos(Entry entryToSave){
        final Entry entryToModify = entryToSave;
        DBAccessCursorWrapper cursor = buildPhotoQuery(entryToModify);
        ArrayList<String> photolist = new ArrayList<String>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                photolist.add(cursor.getString(cursor.getColumnIndex("filename")));
            }
            entryToModify.setPhotoList(photolist);
        }finally {
            cursor.close();
        }

        //this.db.close();

        this.responseHandler.post(new Runnable() {
            @Override
            public void run() {
                onCompleteListener.onCompleteEntry(entryToModify);
            }
        });
    }


    protected void returnEntry(Entry entryToSave){
        final long returnCode = buildInsertQuery(entryToSave);
        //this.db.close();
        this.responseHandler.post(new Runnable() {
            @Override
            public void run() {
                onCompleteListener.onCompleteCode(returnCode);
            }
        });
    }

    protected void saveEntryToDB(Entry entryToSave){
        final long returnCode = buildUpdateQuery(entryToSave);
        //this.db.close();
        this.responseHandler.post(new Runnable() {
            @Override
            public void run() {
                onCompleteListener.onCompleteCode(returnCode);
            }
        });
    }



}
