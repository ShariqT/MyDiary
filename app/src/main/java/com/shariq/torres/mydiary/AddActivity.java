package com.shariq.torres.mydiary;

import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import java.util.ArrayList;

public class AddActivity extends BaseActivity{

    Entry selectedEntry;
    EditText title;
    EditText text;
    Button saveBtn;
    ApiThread apiThread;
    Boolean isEntryNew;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        selectedEntry = getIntent().getParcelableExtra("entry");
        isEntryNew = getIntent().getBooleanExtra("isNew", true);
        Log.d("MyDiary", String.valueOf(selectedEntry));
        title = (EditText) findViewById(R.id.entryTitle);
        text = (EditText) findViewById(R.id.entryText);
        setUpHandlers();
        title.setText(selectedEntry.getTitle());
        text.setText(selectedEntry.getText());
        saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedEntry.setText(text.getText().toString());
                selectedEntry.setTitle(title.getText().toString());
                if(isEntryNew){
                    apiThread.createEntry(selectedEntry);
                }else{
                    apiThread.saveEntry(selectedEntry);
                }
            }
        });



        setUpSettingsMenu();
    }


    protected void setUpHandlers(){
        Handler responseHandler = new Handler();
        apiThread = new ApiThread(responseHandler, getApplicationContext());
        apiThread.start();
        apiThread.getLooper();
        apiThread.setOnCompleteListener(new ApiThread.ApiThreadListener() {
            @Override
            public void onCompleteArray(ArrayList<Entry> entryData) {

            }

            @Override
            public void onCompleteCode(long returnCode) {
                Toast.makeText(AddActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleteEntry(Entry entry) {
                Toast.makeText(AddActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }




}
