package com.shariq.torres.mydiary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Torres on 3/5/2016.
 */
public class Entry implements Parcelable {

    private boolean hasPhotos;
    private String text;
    private String title;
    private Date entryDate;
    private int id;

    private ArrayList<String> photoList = new ArrayList<String>();

    public Entry(String title, String text, boolean photos){
        this.hasPhotos = photos;
        this.text = text;
        this.title = title;
        this.entryDate = new Date();
    }

    public Entry(Parcel in){
        String[] data = new String[6];
        in.readStringArray(data);
        this.hasPhotos = Boolean.valueOf(data[0]);
        this.text = data[1];
        this.title = data[2];
        this.entryDate = new Date(Long.parseLong(data[3]));
        this.setPhotoList(new ArrayList<String>(Arrays.asList(data[4].split(","))));
        this.id = Integer.valueOf(data[5]);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        String photolistString = "";
        for(int i = 0; i < this.photoList.size(); i++){
            if(i == this.photoList.size() - 1){
                photolistString += this.photoList.get(i);
            }else{
                photolistString += this.photoList.get(i) + ",";
            }
        }
        dest.writeStringArray(new String[]{String.valueOf(this.hasPhotos), this.text, this.title, String.valueOf(this.entryDate.getTime()), photolistString, String.valueOf(this.id)});

    }


    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>(){
        @Override
        public Entry[] newArray(int size){
            return new Entry[size];
        }

        @Override
        public Entry createFromParcel(Parcel source){
            return new Entry(source);
        }
    };


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHasPhotos() {
        return hasPhotos;
    }

    public void setHasPhotos(boolean hasPhotos) {
        this.hasPhotos = hasPhotos;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<String> photoList) {
        this.photoList = photoList;
        this.hasPhotos = true;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }
}
