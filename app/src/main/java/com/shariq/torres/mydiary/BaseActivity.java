package com.shariq.torres.mydiary;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Torres on 3/11/2016.
 */
public class BaseActivity extends AppCompatActivity {
    protected ImageButton menuBtn;
    protected DrawerLayout drawer;



    protected void setUpSettingsMenu(){
        menuBtn = (ImageButton) findViewById(R.id.menuBtn);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawer.openDrawer(Gravity.RIGHT);
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, Settings.menu);
        ListView navList = (ListView) findViewById(R.id.listView);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String message = ( (TextView) view).getText().toString();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }





}
