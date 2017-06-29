package com.milanparikh.hotelmonitor.Client;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ParallelExecutorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.Master.MasterSetup;
import com.milanparikh.hotelmonitor.R;
import com.milanparikh.hotelmonitor.SettingsActivity;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

public class Client extends AppCompatActivity {
    ParseQuery query;
    Spinner spinner;
    ClientRoomListAdapter adapter;
    ParseLiveQueryClient parseLiveQueryClient;
    SubscriptionHandling<ParseObject> subscriptionHandling;
    Bundle mBundle;
    String username;
    String settingsPassword;
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
                query.whereContains("room", "R1");
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
        spinner.setBackgroundTintList(getColorStateList(R.color.white));
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.floor_list, R.layout.custom_simple_spinner_item);
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
                checkPassword("master_setup");
                return true;
            case R.id.logout_item:
                ParseUser.logOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkPassword(final String scenario) {
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                if(e==null){
                }else {
                    config = ParseConfig.getCurrentConfig();
                }
                settingsPassword = config.getString("settings_password");
            }
        });
        final AlertDialog.Builder passwordBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.password_dialog, null);
        passwordBuilder.setView(dialogView);

        final EditText dialogPasswordEditText = (EditText)dialogView.findViewById(R.id.dialog_password_edittext);

        passwordBuilder.setTitle("Enter Password");
        passwordBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String submittedPassword = dialogPasswordEditText.getText().toString();
                correctPassword = (settingsPassword.equals(submittedPassword));
                if(correctPassword){
                    optionsActions(scenario);
                }
            }
        });
        passwordBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog passwordDialog = passwordBuilder.create();
        passwordDialog.show();
        final Button posButton = passwordDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        dialogPasswordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)) {
                    posButton.performClick();
                }

                return false;
            }
        });
    }

    public void optionsActions(String scenario){
        switch(scenario) {
            case "master_setup":
                correctPassword = false;
                Intent masterSetupIntent = new Intent(this, MasterSetup.class);
                startActivity(masterSetupIntent);
                break;

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
