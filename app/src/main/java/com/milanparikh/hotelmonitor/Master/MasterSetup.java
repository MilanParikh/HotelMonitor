package com.milanparikh.hotelmonitor.Master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class MasterSetup extends AppCompatActivity {

    Button floor1button;
    Button floor2button;
    Button floor3button;
    Button enableMasterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_setup);
        Toolbar toolbar = (Toolbar)findViewById(R.id.master_setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        floor1button = (Button)findViewById(R.id.gen_1_button);
        floor1button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=1; i<13; i++) {
                    ParseObject roomList = new ParseObject("RoomList");
                    roomList.put("room","R"+ Integer.toString(100+i));
                    roomList.put("clean",0);
                    roomList.put("status",0);
                    roomList.saveInBackground();
                }
            }
        });
        floor2button = (Button)findViewById(R.id.gen_2_button);
        floor2button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=1; i<25; i++) {
                    ParseObject roomList = new ParseObject("RoomList");
                    roomList.put("room","R"+ Integer.toString(200+i));
                    roomList.put("clean",0);
                    roomList.put("status",0);
                    roomList.saveInBackground();
                }
            }
        });
        floor3button = (Button)findViewById(R.id.gen_3_button);
        floor3button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=1; i<25; i++) {
                    ParseObject roomList = new ParseObject("RoomList");
                    roomList.put("room","R"+ Integer.toString(300+i));
                    roomList.put("clean",0);
                    roomList.put("status",0);
                    roomList.saveInBackground();
                }
            }
        });
        enableMasterButton = (Button)findViewById(R.id.enable_master_button);
        enableMasterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.put("MasterMode", 1);
                currentUser.saveInBackground();
            }
        });

    }
}
