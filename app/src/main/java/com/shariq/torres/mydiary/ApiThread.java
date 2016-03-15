package com.shariq.torres.mydiary;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Torres on 3/14/2016.
 */
public class ApiThread extends HandlerThread {
    //private Context uiContext;
    private Handler requestHandler;
    private Handler responseHandler;
    private ApiThreadListener onCompleteListener;
    private final int GET_ENTRIES = 0;

    public ApiThread(Handler responseHandler){
        super("APIThread");
        this.responseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared(){
        requestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                    case GET_ENTRIES:
                        getListOfEntries();
                        break;
                }
            }
        };
    }

    public interface ApiThreadListener {
        void onComplete(ArrayList<Entry> entryData);
    }

    public void setOnCompleteListener(ApiThreadListener listener){
        onCompleteListener = listener;
    }


    public void queueTask(String type){
        requestHandler.obtainMessage(GET_ENTRIES).sendToTarget();
    }

    protected void getListOfEntries(){
        final ArrayList<Entry> entryData = new ArrayList<Entry>();
        for(int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                entryData.add(new Entry("Even stuff", "This is a even title from background", true));
            } else {
                entryData.add(new Entry("Odd stuff", "This is a odd title from background", false));
            }

        }

        this.responseHandler.post(new Runnable() {
            @Override
            public void run() {
                onCompleteListener.onComplete(entryData);
            }
        });
    }



}
