package com.milanparikh.hotelmonitor.Client;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.milanparikh.hotelmonitor.Other.SettingsActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.util.ArrayList;
import java.util.List;

public class Client extends AppCompatActivity {
    ParseQuery query;
    Spinner spinner;
    ClientRoomListAdapter adapter;
    ParseLiveQueryClient parseLiveQueryClient;
    SubscriptionHandling<ParseObject> subscriptionHandling;
    Bundle mBundle;
    String username;
    String adminPassword;
    Boolean correctPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mBundle = savedInstanceState;
        }
        setContentView(R.layout.activity_client);

        Toolbar toolbar = (Toolbar) findViewById(R.id.client_toolbar);
        setSupportActionBar(toolbar);

        ParseUser user = ParseUser.getCurrentUser();
        username = user.getUsername();

        TextView toolbarUsername = (TextView)findViewById(R.id.toolbar_username);
        toolbarUsername.setText(username);

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        adapter = new ClientRoomListAdapter(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                query = new ParseQuery("RoomList");
                query.orderByAscending("room");
                query.whereEqualTo("floor", 1);
                query.whereEqualTo("clean",0);
                query.whereEqualTo("current_name", username);
                return query;
            }
        });

        subscriptionHandling = parseLiveQueryClient.subscribe(query);
        subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
            @Override
            public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                adapter.loadObjects();
            }
        });
        adapter.setObjectsPerPage(60);

        ListView listView = (ListView) findViewById(R.id.client_room_list);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_menu, menu);
        MenuItem menuSpinner = menu.findItem(R.id.floor_spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(menuSpinner);
        spinner.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.white));
        ParseQuery<ParseObject> floorQuery = new ParseQuery<ParseObject>("Floors");
        floorQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null){
                    ArrayList<String> floorList = new ArrayList<>();
                    for(ParseObject object : list){
                        floorList.add("Floor " + Integer.toString(object.getInt("floor")));
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_simple_spinner_item, floorList);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);
                    if (mBundle != null) {
                        spinner.setSelection(mBundle.getInt("spinner_position"));
                    }
                }else{

                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parseLiveQueryClient.unsubscribe(query);
                query.whereEqualTo("floor",position+1);
                adapter.loadObjects();
                subscriptionHandling = parseLiveQueryClient.subscribe(query);
                subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
                    @Override
                    public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                        adapter.loadObjects();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("spinner_position",spinner.getSelectedItemPosition());
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
        finish();
    }
}
