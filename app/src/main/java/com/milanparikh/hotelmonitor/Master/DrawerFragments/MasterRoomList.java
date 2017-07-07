package com.milanparikh.hotelmonitor.Master.DrawerFragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;

import com.milanparikh.hotelmonitor.Master.MasterExport;
import com.milanparikh.hotelmonitor.Master.MasterSetup;
import com.milanparikh.hotelmonitor.R;
import com.milanparikh.hotelmonitor.Other.SettingsActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class MasterRoomList extends Fragment {
    ParseQuery query;
    Spinner spinner;
    MasterRoomListAdapter<ParseObject> adapter;
    ParseLiveQueryClient parseLiveQueryClient;
    SubscriptionHandling<ParseObject> subscriptionHandling;
    Bundle mBundle;
    ListView listView;
    ParseObject pObject;
    String pObjectID;
    AppCompatActivity activity;


    public MasterRoomList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_master_room_list, container, false);

        activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.master_room_list_toolbar);
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        listView = (ListView) view.findViewById(R.id.master_room_list);

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        adapter = new MasterRoomListAdapter<ParseObject>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                query = new ParseQuery("RoomList");
                query.orderByAscending("room");
                query.whereEqualTo("floor", 1);
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

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.master_dashboard_menu, menu);

        MenuItem menuSpinner = menu.findItem(R.id.floor_spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(menuSpinner);
        spinner.setBackgroundTintList(activity.getColorStateList(R.color.white));
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.floor_list, R.layout.custom_simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        if (this.mBundle != null) {
            spinner.setSelection(mBundle.getInt("spinner_position"));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parseLiveQueryClient.unsubscribe(query);
                query.whereEqualTo("floor", position+1);
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.master_setup:
                Intent masterSetupIntent = new Intent(getContext(), MasterSetup.class);
                startActivity(masterSetupIntent);
                return true;
            case R.id.master_export:
                Intent masterExportIntent = new Intent(getContext(), MasterExport.class);
                startActivity(masterExportIntent);
                return true;
            case R.id.logout_item:
                ParseUser.logOut();
                activity.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, v, contextMenuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
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
                pObject.put("membership", 0);
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
        AlertDialog.Builder membershipBuilder = new AlertDialog.Builder(getContext());
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
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            this.mBundle=savedInstanceState;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinner_position",spinner.getSelectedItemPosition());
    }

}
