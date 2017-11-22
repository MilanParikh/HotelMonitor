package com.milanparikh.hotelmonitor.Master.DrawerFragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.milanparikh.hotelmonitor.Client.ClientCheckList;
import com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters.MasterAvailabilityListAdapter;
import com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters.MasterAvailabilityRoomListAdapter;
import com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters.MasterRoomListAdapter;
import com.milanparikh.hotelmonitor.Master.MasterExport;
import com.milanparikh.hotelmonitor.R;
import com.milanparikh.hotelmonitor.Other.SettingsActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MasterRoomList extends Fragment {
    ParseQuery roomQuery;
    Spinner spinner;
    MasterRoomListAdapter<ParseObject> roomListAdapter;
    ParseLiveQueryClient parseLiveQueryClient;
    SubscriptionHandling<ParseObject> subscriptionHandling;
    Bundle mBundle;
    ListView roomListView;
    ParseObject pObject;
    String pObjectID;
    AppCompatActivity activity;
    ListView availableListView;
    MasterAvailabilityListAdapter<ParseObject> availabilityListAdapter;
    ParseQuery availabilityQuery;
    String availableRoomType="";
    ListView availableRoomListView;
    MasterAvailabilityRoomListAdapter<ParseObject> availabilityRoomListAdapter;
    ParseQuery availableRoomQuery;
    Intent checklistIntent;
    Bundle extras;

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

        roomListView = (ListView) view.findViewById(R.id.master_room_list);

        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        roomListAdapter = new MasterRoomListAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                roomQuery = new ParseQuery("RoomList");
                roomQuery.orderByAscending("room");
                roomQuery.whereEqualTo("floor", 1);
                return roomQuery;
            }
        });

        roomListAdapter.setObjectsPerPage(60);


        roomListView.setAdapter(roomListAdapter);
        registerForContextMenu(roomListView);
        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.showContextMenu();
            }
        });

        int orientation = getResources().getConfiguration().orientation;
        if(Configuration.ORIENTATION_LANDSCAPE==orientation){
            availableListView = (ListView)view.findViewById(R.id.availability_listview);
            availabilityListAdapter = new MasterAvailabilityListAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
                @Override
                public ParseQuery<ParseObject> create() {
                    availabilityQuery = new ParseQuery("RoomTypes");
                    availabilityQuery.orderByAscending("acronym");
                    return availabilityQuery;
                }
            });
            availableListView.setAdapter(availabilityListAdapter);
            availableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ParseObject typeObject = (ParseObject)parent.getItemAtPosition(position);
                    availableRoomType = typeObject.getString("acronym");
                    availableRoomQuery.whereEqualTo("type", availableRoomType);
                    availabilityRoomListAdapter.loadObjects();
                }
            });

            availableRoomListView = (ListView)view.findViewById(R.id.availability_room_listview);
            availabilityRoomListAdapter = new MasterAvailabilityRoomListAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
                @Override
                public ParseQuery<ParseObject> create() {
                    availableRoomQuery = new ParseQuery("RoomList");
                    availableRoomQuery.orderByAscending("room");
                    availableRoomQuery.whereEqualTo("type", availableRoomType);
                    availableRoomQuery.whereEqualTo("clean",4);
                    return availableRoomQuery;
                }
            });
            availableRoomListView.setAdapter(availabilityRoomListAdapter);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.master_dashboard_menu, menu);

        MenuItem menuSpinner = menu.findItem(R.id.floor_spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(menuSpinner);
        spinner.setBackgroundTintList(activity.getColorStateList(R.color.white));
        ParseQuery<ParseObject> floorQuery = new ParseQuery<ParseObject>("Floors");
        floorQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null){
                    ArrayList<String> floorList = new ArrayList<>();
                    for(ParseObject object : list){
                        floorList.add("Floor " + Integer.toString(object.getInt("floor")));
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_simple_spinner_item, floorList);
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
                parseLiveQueryClient.unsubscribe(roomQuery);
                roomQuery.whereEqualTo("floor", position+1);
                roomListAdapter.loadObjects();
                subscriptionHandling = parseLiveQueryClient.subscribe(roomQuery);
                subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
                    @Override
                    public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                        roomListAdapter.loadObjects();
                        int orientation = getResources().getConfiguration().orientation;
                        if(Configuration.ORIENTATION_LANDSCAPE==orientation){
                            availabilityListAdapter.loadObjects();
                            availabilityRoomListAdapter.loadObjects();
                        }
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
            case R.id.master_export:
                Intent masterExportIntent = new Intent(getContext(), MasterExport.class);
                startActivity(masterExportIntent);
                return true;
            case R.id.logout_item:
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.remove("user");
                installation.saveInBackground();
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
        if(cleanStatus!=2){
            contextMenu.findItem(R.id.check_room).setEnabled(false);
        }
        switch (cleanStatus) {
            case 2:
                contextMenu.findItem(R.id.set_clean).setChecked(true);
                break;
            case 6:
                contextMenu.findItem(R.id.set_out_order).setChecked(true);
                break;
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.check_in_out_date:
                showDatePicker("in");
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
            case R.id.check_room:
                checklistIntent = new Intent(getContext(),ClientCheckList.class);
                extras = new Bundle();
                extras.putString("objectID",pObject.getObjectId());
                extras.putParcelable("roomListObject", pObject);
                extras.putString("source", "master");
                ParseQuery<ParseObject> maintenanceListQuery = ParseQuery.getQuery("MaintenanceList");
                maintenanceListQuery.whereEqualTo("room", pObject.getInt("room"));
                maintenanceListQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        ParseObject maintenanceListObject = objects.get(0);
                        extras.putParcelable("maintenanceListObject", maintenanceListObject);
                        checklistIntent.putExtras(extras);
                        startActivityForResult(checklistIntent, 1);
                    }
                });

                return true;
            case R.id.set_clean:
                pObject.put("clean", 2);
                pObject.remove("current_name");
                pObject.saveInBackground();
                return true;
            case R.id.set_out_order:
                pObject.put("clean", 6);
                pObject.remove("current_name");
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
                            getTargetFragment().onActivityResult(getTargetRequestCode(), 1, null);
                            break;
                        case "out":
                            object.put("checkoutdate", date);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), 2, null);
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
        datePickerFragment.setTargetFragment(this, 2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            this.mBundle=savedInstanceState;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==1){
            roomListAdapter.loadObjects();
            int orientation = getResources().getConfiguration().orientation;
            if(Configuration.ORIENTATION_LANDSCAPE==orientation){
                availabilityListAdapter.loadObjects();
            }
            subscriptionHandling = parseLiveQueryClient.subscribe(roomQuery);
            subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
                @Override
                public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                    roomListAdapter.loadObjects();
                    int orientation = getResources().getConfiguration().orientation;
                    if(Configuration.ORIENTATION_LANDSCAPE==orientation){
                        availabilityListAdapter.loadObjects();
                        availabilityRoomListAdapter.loadObjects();
                    }
                }
            });
        }else if (requestCode==2){
            if(resultCode==1){
                showDatePicker("out");
            }else if (resultCode==2){
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinner_position",spinner.getSelectedItemPosition());
        parseLiveQueryClient.unsubscribe(roomQuery);
    }

}
