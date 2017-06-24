package com.milanparikh.hotelmonitor.Master;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.milanparikh.hotelmonitor.R;
import com.milanparikh.hotelmonitor.SettingsActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.util.Calendar;
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
    String pObjectID;

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
        pObjectID = pObject.getObjectId();
        int cleanStatus = pObject.getInt("clean");
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
            case 3:
                contextMenu.findItem(R.id.set_private).setChecked(true);
                break;
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.check_in_date:
                showDatePicker("in");
                return true;
            case R.id.check_out_date:
                showDatePicker("out");
                return true;
            case R.id.clear_duration:
                pObject.remove("checkindate");
                pObject.remove("checkoutdate");
                pObject.saveInBackground();
                return true;
            case R.id.add_membership:
                showMembershipDialog();
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
            case R.id.set_private:
                pObject.put("clean", 3);
                pObject.saveInBackground();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void showMembershipDialog() {
        AlertDialog.Builder membershipBuilder = new AlertDialog.Builder(this);
        membershipBuilder.setTitle(R.string.select_membership);
        membershipBuilder.setItems(R.array.membership_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pObject.put("membership",which);
                pObject.saveInBackground();
            }
        });

        AlertDialog dialog = membershipBuilder.create();
        dialog.show();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        String inout;
        String objectID;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            Bundle args = getArguments();
            inout = args.getString("inout");
            objectID = args.getString("id");
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            final String date = Integer.toString(month+1) + "/" + Integer.toString(day) + "/" + Integer.toString(year);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("RoomList");
            query.getInBackground(objectID, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    switch(inout){
                        case "in":
                            object.put("checkindate", date);
                            break;
                        case "out":
                            object.put("checkoutdate", date);
                            break;
                    }
                    object.saveInBackground();
                }
            });
        }

    }

    public void showDatePicker(String inout) {
        DialogFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString("inout", inout);
        args.putString("id",pObjectID);
        datePickerFragment.setArguments(args);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.master_menu, menu);
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
                Intent masterSetupIntent = new Intent(this, MasterSetup.class);
                startActivity(masterSetupIntent);
                return true;
            case R.id.master_export:
                Intent masterExportIntent = new Intent(this, MasterExport.class);
                startActivity(masterExportIntent);
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
        parseLiveQueryClient.unsubscribe(query);
        ParseUser user = ParseUser.getCurrentUser();
        if (user!=null) {
            user.logOutInBackground();
        }
        finish();
    }
}
