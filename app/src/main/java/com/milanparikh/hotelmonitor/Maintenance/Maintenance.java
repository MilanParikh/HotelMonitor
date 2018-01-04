package com.milanparikh.hotelmonitor.Maintenance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.milanparikh.hotelmonitor.Other.SettingsActivity;
import com.milanparikh.hotelmonitor.R;
import com.parse.ParseInstallation;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

public class Maintenance extends AppCompatActivity {
    ListView roomList;
    MaintenanceRoomListAdapter<ParseObject> maintenanceRoomListAdapter;
    ParseQuery<ParseObject> maintenanceQuery;
    ParseLiveQueryClient parseLiveQueryClient;
    SubscriptionHandling<ParseObject> subscriptionHandling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.maintenance_toolbar);
        setSupportActionBar(toolbar);

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        roomList = (ListView)findViewById(R.id.maintenance_listview);
        maintenanceRoomListAdapter = new MaintenanceRoomListAdapter<>(getApplicationContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                maintenanceQuery = new ParseQuery<>("RoomList");
                maintenanceQuery.orderByAscending("room");
                maintenanceQuery.whereGreaterThanOrEqualTo("clean", 5);
                maintenanceQuery.whereLessThanOrEqualTo("clean", 6);
                return maintenanceQuery;
            }
        });
        roomList.setAdapter(maintenanceRoomListAdapter);

        subscriptionHandling = parseLiveQueryClient.subscribe(maintenanceQuery);
        subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
            @Override
            public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                maintenanceRoomListAdapter.loadObjects();
                maintenanceRoomListAdapter.notifyDataSetChanged();
                maintenanceRoomListAdapter.loadObjects();
            }
        });
        maintenanceRoomListAdapter.setObjectsPerPage(60);

        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject roomObject = (ParseObject)parent.getItemAtPosition(position);
                String objID = roomObject.getObjectId();
                Intent launchCheckList = new Intent(getApplicationContext(), MaintenanceCheckList.class);
                Bundle extras = new Bundle();
                extras.putString("objectID", objID);
                extras.putParcelable("roomListObject", roomObject);
                launchCheckList.putExtras(extras);
                startActivityForResult(launchCheckList, 1);
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
            case R.id.logout_item:
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.remove("user");
                installation.saveInBackground();
                ParseUser.logOutInBackground();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==1){
            maintenanceRoomListAdapter.loadObjects();
            subscriptionHandling = parseLiveQueryClient.subscribe(maintenanceQuery);
            subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
                @Override
                public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                    maintenanceRoomListAdapter.loadObjects();
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        parseLiveQueryClient.unsubscribe(maintenanceQuery);
    }

    @Override
    public void onBackPressed() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user!=null) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.remove("user");
            installation.saveInBackground();
            user.logOutInBackground();
        }
        parseLiveQueryClient.unsubscribe(maintenanceQuery);
        finish();
    }

}
