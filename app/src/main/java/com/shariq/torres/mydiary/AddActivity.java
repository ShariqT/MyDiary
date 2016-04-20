package com.shariq.torres.mydiary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddActivity extends BaseActivity implements DeletePhotoAlert.OnFragmentInteractionListener, DeleteEntryAlert.OnEntryInteractionListener{

    Entry selectedEntry;
    EditText title;
    EditText text;
    Button saveBtn;
    ApiThread apiThread;
    Boolean isEntryNew;
    ImageButton cameraBtn;
    RecyclerView gridImages;
    EntryImageAdapter adapter;


    public static int REQUEST_PHOTOS = 1;
    boolean isInitConfig = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        selectedEntry = getIntent().getParcelableExtra("entry");
        isEntryNew = getIntent().getBooleanExtra("isNew", true);
        title = (EditText) findViewById(R.id.entryTitle);
        text = (EditText) findViewById(R.id.entryText);
        cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);

        title.setText(selectedEntry.getTitle());
        text.setText(selectedEntry.getText());
        saveBtn = (Button) findViewById(R.id.saveBtn);
        gridImages = (RecyclerView) findViewById(R.id.thumbnailGrid);
        //setUpHandlers();


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

        cameraBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, REQUEST_PHOTOS);
                }else{
                    Toast.makeText(getApplicationContext(),"Not able to find file system", Toast.LENGTH_LONG).show();
                }

            }
        });



        setUpSettingsMenu();

    }

    @Override
    protected void onStart(){
        super.onStart();
        setUpHandlers();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //apiThread.getDb().close();
        apiThread.quit();
    }

    @Override
    protected void onStop(){
        super.onStop();
        //apiThread.quit();
        getIntent().putExtra("entry", selectedEntry);
    }


    /*@Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("entry", selectedEntry);
    }*/


    protected void onActivityResult(int RequestCode, int ResultCode, Intent intent){

        if(ResultCode == RESULT_OK){
            addImageThumbnail(intent.getData());
        }else{
            Toast.makeText(this, "someting went wrong", Toast.LENGTH_SHORT).show();
        }


    }


    //@Override
   // protected void onResume(){
        //super.onResume();
        /*if(getIntent().getParcelableExtra("entry") != null){
            selectedEntry = getIntent().getParcelableExtra("entry");
            //setUpHandlers();
        }*/

    //}


    protected void addImageThumbnail(Uri data){
       selectedEntry.getPhotoList().add(data.toString());
        //photolist.add(data.toString());
        if(selectedEntry.getPhotoList().size() == 1){
            adapter = new EntryImageAdapter(selectedEntry.getPhotoList());
            gridImages.setAdapter(adapter);
            gridImages.setLayoutManager(new GridLayoutManager(this, 3));
            gridImages.setVisibility(View.VISIBLE);
            gridImages.setMinimumHeight(100);
        }else {
            adapter.data = selectedEntry.getPhotoList();
            //adapter.notifyItemInserted(adapter.data.size() - 1);
            adapter.notifyDataSetChanged();
            double rows = Math.ceil( (double) selectedEntry.getPhotoList().size() / 3.0);
            if(rows == 0.0){
                gridImages.setMinimumHeight(250);
            }else{
                gridImages.setMinimumHeight( (int)rows * 250);
            }

        }

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
                Toast.makeText(AddActivity.this, "Complete!", Toast.LENGTH_SHORT).show();
                if(returnCode == -1000){
                    apiThread.getEntryPhotos(selectedEntry);
                }

                if(returnCode == -2000){
                    Intent i = new Intent(AddActivity.this, EntryActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCompleteEntry(Entry entry) {
                //Toast.makeText(AddActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                //photolist = entry.getPhotoList();
                selectedEntry.setPhotoList( entry.getPhotoList());
                if(selectedEntry.getPhotoList().size() > 0) {
                    adapter = new EntryImageAdapter(selectedEntry.getPhotoList());
                    gridImages.setAdapter(adapter);
                    gridImages.setLayoutManager(new GridLayoutManager(AddActivity.this, 3));
                    double rows = Math.ceil( (double)selectedEntry.getPhotoList().size() / 3.0);
                    if(rows == 0.0){
                        gridImages.setMinimumHeight(250);
                    }else {
                        gridImages.setMinimumHeight((int) rows * 250);
                    }
                    gridImages.setVisibility(View.VISIBLE);
                }else{
                    gridImages.setVisibility(View.INVISIBLE);
                    gridImages.setMinimumHeight(10);
                }
            }
        });

        if(isInitConfig) {
            apiThread.getEntryPhotos(selectedEntry);
            isInitConfig = false;
        }
    }


    public void onFragmentInteraction(String src){
        apiThread.deletePhoto(selectedEntry.getId(), src);
    }

    public void onEntryDialogInteraction(int id){
        apiThread.deleteEntry(id);
    }

    @Override
    protected void onDeleteEntryFromSettingMenu(){
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment dialog = DeleteEntryAlert.newInstance(selectedEntry.getId());
        dialog.show(fm, "deleteEntry");
    }


    public class EntryImageHolder extends RecyclerView.ViewHolder
    {
        public ImageView entryImg;
        public TextView entrySrc;
        public EntryImageHolder(View itemView){
            super(itemView);
            entryImg = (ImageView) itemView.findViewById(R.id.entryImg);
            entrySrc = (TextView) itemView.findViewById(R.id.entrySrc);
        }

        public void bindValues(String filePath){
            try {
                Uri fileUri = Uri.parse(filePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(AddActivity.this.getContentResolver().openInputStream(fileUri), null, options);
                options.inSampleSize =  calculateSampleSize(options, 100, 100);
                options.inJustDecodeBounds = false;

                entryImg.setImageBitmap(BitmapFactory.decodeStream(AddActivity.this.getContentResolver().openInputStream(fileUri), null, options));
                entrySrc.setText(filePath);

                entryImg.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        FragmentManager fm = getSupportFragmentManager();
                        DialogFragment dialog = DeletePhotoAlert.newInstance( (String)entrySrc.getText());
                        dialog.show(fm, "deletePhoto");



                    }
                });
            }catch (java.io.FileNotFoundException e){
                Log.d("AddActivity", "file error was " + e.getMessage());
                Toast.makeText(AddActivity.this, "Error getting file", Toast.LENGTH_SHORT).show();
            }
        }


        protected int calculateSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth){

            final int height = options.outHeight;
            final int width = options.outWidth;
            int sampleSize = 1;
            if( height > reqHeight || width > reqWidth){
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                while((halfHeight / sampleSize) > reqHeight && (halfWidth / sampleSize) > reqWidth){
                    sampleSize *= 2;
                }
            }

            return sampleSize;
        }
    }


    public class EntryImageAdapter extends RecyclerView.Adapter<EntryImageHolder>
    {
        public ArrayList<String> data;

        public EntryImageAdapter(ArrayList<String> data){
            this.data = data;
        }
        @Override
        public EntryImageHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(AddActivity.this);
            View view = layoutInflater.inflate(R.layout.entry_image, parent, false);
            return new EntryImageHolder(view);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onBindViewHolder(EntryImageHolder holder, int position){
            holder.bindValues(this.data.get(position));
        }
    }




}
