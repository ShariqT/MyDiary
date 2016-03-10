package com.shariq.torres.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class EntryActivity extends AppCompatActivity {
    RecyclerView userEntries;
    ArrayList<Entry> entryData;
    EntryAdapter arrayAdapter;
    ImageButton addEntryBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        updateUI();
        addEntryBtn = (ImageButton) findViewById(R.id.addNewEntry);
        addEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(EntryActivity.this, AddActivity.class);
               startActivity(i);
            }
        });
    }


    private void updateUI(){
        entryData = new ArrayList<Entry>();
        for(int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                entryData.add(new Entry("Even stuff", "This is a even title", true));
            } else {
                entryData.add(new Entry("Odd stuff", "This is a odd title", false));
            }

        }
        userEntries = (RecyclerView) findViewById(R.id.entryList);
        userEntries.setLayoutManager(new LinearLayoutManager(this));
        arrayAdapter = new EntryAdapter(entryData);
        userEntries.setAdapter(arrayAdapter);
    }


    private class EntryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView entryTitle;
        public ImageView cameraIcon;
        public ImageView bookIcon;
        private Entry selectedEntry;

        public EntryHolder(View itemView){
            super(itemView);
            this.entryTitle = (TextView) itemView.findViewById(R.id.entryTitle);
            this.cameraIcon = (ImageView) itemView.findViewById(R.id.camera_icon);
            this.bookIcon = (ImageView) itemView.findViewById(R.id.book_icon);
            itemView.setOnClickListener(this);

        }

        public void bindValues(Entry val){
            selectedEntry = val;
            Log.d("MyDiary", "in the bind values function: " + String.valueOf(selectedEntry));
            entryTitle.setText(val.getTitle());
            if(val.isHasPhotos() == false){
                cameraIcon.setVisibility(View.INVISIBLE);
            }
        }
        @Override
        public void onClick(View v){
            Log.d("MyDiary", "in the onclick function: " + String.valueOf(selectedEntry));
            Intent i = new Intent(EntryActivity.this, AddActivity.class);
            i.putExtra("entry", selectedEntry);
            startActivity(i);
        }
    }


    private class EntryAdapter extends RecyclerView.Adapter<EntryHolder>{
        private ArrayList<Entry> data;
        public EntryAdapter(ArrayList<Entry> entries){
            this.data = entries;
        }

        @Override
        public EntryHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(EntryActivity.this);
            View view = layoutInflater.inflate(R.layout.entry_item, parent, false);
            return new EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(EntryHolder holder, int position){
            Entry entry = data.get(position);
            holder.bindValues(entry);
        }

        @Override
        public int getItemCount(){
            return data.size();
        }
    }

}
