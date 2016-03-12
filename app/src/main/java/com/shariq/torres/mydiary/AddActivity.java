package com.shariq.torres.mydiary;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

public class AddActivity extends BaseActivity{

    Entry selectedEntry;
    EditText title;
    EditText text;
    Button saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        selectedEntry = getIntent().getParcelableExtra("entry");
        Log.d("MyDiary",String.valueOf(selectedEntry));
        title = (EditText) findViewById(R.id.entryTitle);
        text = (EditText) findViewById(R.id.entryText);

        title.setText(selectedEntry.getTitle());
        text.setText(selectedEntry.getText());
        saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(AddActivity.this,"Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        setUpSettingsMenu();
    }




}
