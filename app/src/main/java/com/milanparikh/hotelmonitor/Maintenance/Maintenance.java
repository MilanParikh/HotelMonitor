package com.milanparikh.hotelmonitor.Maintenance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.milanparikh.hotelmonitor.Other.SettingsActivity;
import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

public class Maintenance extends AppCompatActivity {
    ListView roomList;
    MaintenanceAdapter<ParseObject> maintenanceAdapter;
    ParseQuery<ParseObject> maintenanceQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        roomList = (ListView)findViewById(R.id.maintenance_listview);
        maintenanceAdapter = new MaintenanceAdapter<>(getApplicationContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                maintenanceQuery = new ParseQuery<>("");
                maintenanceQuery.orderByAscending("room");
                return maintenanceQuery;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maintenance_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.logout_item:
                ParseUser.logOutInBackground();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user!=null) {
            user.logOutInBackground();
        }
        finish();
    }

}
