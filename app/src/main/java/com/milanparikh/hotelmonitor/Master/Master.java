package com.milanparikh.hotelmonitor.Master;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.milanparikh.hotelmonitor.R;
import com.milanparikh.hotelmonitor.SettingsActivity;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.util.List;
import java.util.Objects;

public class Master extends AppCompatActivity {
    ParseQuery query;
    Spinner spinner;
    MasterRoomListAdapter<ParseObject> adapter;
    ParseLiveQueryClient parseLiveQueryClient;
    SubscriptionHandling<ParseObject> subscriptionHandling;
    Bundle mBundle;
    ListView listView;
    ParseObject pObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.mBundle = savedInstanceState;
        }

        setContentView(R.layout.activity_master);
        Toolbar toolbar = (Toolbar) findViewById(R.id.master_toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.master_room_list);

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        adapter = new MasterRoomListAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                query = new ParseQuery("RoomList");
                query.orderByAscending("room");
                query.whereContains("room", "R1");
                return query;
            }
        });

        adapter.setObjectsPerPage(60);


        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.showContextMenu();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, v, contextMenuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.master_roomlist_context_menu, contextMenu);
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
        pObject = (ParseObject) lv.getAdapter().getItem(info.position);
        int cleanStatus = pObject.getInt("clean");
        int guestStatus = pObject.getInt("status");
        switch (guestStatus) {
            case 0:
                contextMenu.findItem(R.id.set_due_out).setChecked(true);
                break;
            case 1:
                contextMenu.findItem(R.id.set_stay_over).setChecked(true);
                break;
        }
        switch (cleanStatus) {
            case 0:
                contextMenu.findItem(R.id.set_dirty).setChecked(true);
                break;
            case 1:
                contextMenu.findItem(R.id.set_in_progress).setChecked(true);
                break;
            case 2:
                contextMenu.findItem(R.id.set_clean).setChecked(true);
                break;
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.set_due_out:
                pObject.put("status", 0);
                pObject.saveInBackground();
                return true;
            case R.id.set_stay_over:
                pObject.put("status", 1);
                pObject.saveInBackground();
                return true;
            case R.id.set_dirty:
                pObject.put("clean", 0);
                pObject.saveInBackground();
                return true;
            case R.id.set_in_progress:
                pObject.put("clean", 1);
                pObject.saveInBackground();
                return true;
            case R.id.set_clean:
                pObject.put("clean", 2);
                pObject.saveInBackground();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.master_menu, menu);
        MenuItem menuSpinner = menu.findItem(R.id.floor_spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(menuSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.floor_list, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        if (this.mBundle != null) {
            spinner.setSelection(mBundle.getInt("spinner_position"));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parseLiveQueryClient.unsubscribe(query);
                query.whereContains("room","R"+Integer.toString(position+1));
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
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.master_setup:
                Intent masterSetupIntent = new Intent(this, MasterSetup.class);
                startActivity(masterSetupIntent);
                return true;
            case R.id.logout_item:
                ParseUser.logOut();
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
            user.logOutInBackground();
        }
        finish();
    }
}
