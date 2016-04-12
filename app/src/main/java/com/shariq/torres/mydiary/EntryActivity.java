package com.shariq.torres.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    int nextEntryId;
    EntryAdapter arrayAdapter;
    ImageButton addEntryBtn;
    ApiThread apiThread;
    ArrayList<Entry> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        userEntries = (RecyclerView) findViewById(R.id.entryList);

        //updateUI();
        addEntryBtn = (ImageButton) findViewById(R.id.addNewEntry);
        addEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Entry newEntry = new Entry("", "", false);
                newEntry.setId(nextEntryId);

               Intent i = new Intent(EntryActivity.this, AddActivity.class);
               i.putExtra("entry", newEntry);
                i.putExtra("isNew", true);
               startActivity(i);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        apiThread.quit();
        Log.i("EntryActivity", "Stopped the background thread!");
    }

    @Override
    protected  void onStop(){
        super.onStop();
        apiThread.quit();
        getIntent().putParcelableArrayListExtra("entries", data);
        Log.i("EntryActivity", "Stopped the background thread!");

    }


    @Override
    protected  void onStart(){
        Log.d("EntryActivity", "in the onStart function");
        super.onStart();
        updateUI();
        //arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        if(getIntent().getParcelableArrayListExtra("entries") != null){
            data = getIntent().getParcelableArrayListExtra("entries");
            arrayAdapter = new EntryAdapter(data);
            userEntries.setAdapter(arrayAdapter);
            //have to account for if you are making the first entry
            if(data.size() > 0){
                nextEntryId = data.get(data.size() - 1).getId();
                Log.d("API", "The entry._id for the last item returned in the entryData array is " + String.valueOf(nextEntryId));
            }else{
                nextEntryId = 0;
            }
            nextEntryId = nextEntryId + 1;
            //updateUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("entries", data);
    }

    private void updateUI(){
        Handler responseHandler = new Handler();
        apiThread = new ApiThread(responseHandler, getApplicationContext());
        apiThread.start();
        apiThread.getLooper();
        Log.d("EntryActivity", "Started the background thread!");


        apiThread.setOnCompleteListener(new ApiThread.ApiThreadListener() {
            @Override
            public void onCompleteArray(ArrayList<Entry> entryData) {
                //userEntries = (RecyclerView) findViewById(R.id.entryList);
                userEntries.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                Log.d("API", "On complete returned " + String.valueOf(entryData.size()) + " entries");
                data = entryData;
                arrayAdapter = new EntryAdapter(entryData);
                userEntries.setAdapter(arrayAdapter);
                //have to account for if you are making the first entry
                if(entryData.size() > 0){
                    nextEntryId = entryData.get(entryData.size() - 1).getId();
                    Log.d("API", "The entry._id for the last item returned in the entryData array is " + String.valueOf(nextEntryId));
                }else{
                    nextEntryId = 0;
                }
                nextEntryId = nextEntryId + 1;
            }

            @Override
            public void onCompleteEntry(Entry e){
                //stub
            }

            @Override
            public void onCompleteCode(long e){
                //stub
            }
        });

        apiThread.getEntryList();




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
            i.putExtra("isNew", false);
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
