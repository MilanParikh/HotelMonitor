package com.milanparikh.hotelmonitor.Master.DrawerFragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.milanparikh.hotelmonitor.Master.MasterExport;
import com.milanparikh.hotelmonitor.Master.MasterSetup;
import com.milanparikh.hotelmonitor.R;
import com.milanparikh.hotelmonitor.Other.SettingsActivity;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MasterEmployeeList extends Fragment {

    AppCompatActivity activity;
    ListView employeeListView;
    ListView roomListView;
    MasterEmployeeListAdapter<ParseUser> employeeListAdapter;
    ParseQuery<ParseUser> employeeListQuery;
    MasterEmployeeRoomListAdapter<ParseObject> roomListAdapter;
    ParseQuery<ParseObject> roomListQuery;
    ParseUser employeeUser;
    String selectedEmployeeName="";
    ParseObject roomObject;

    public MasterEmployeeList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_master_employee_list, container, false);

        activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.master_employee_list_toolbar);
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        
        employeeListView = (ListView)view.findViewById(R.id.employee_listview);
        roomListView = (ListView)view.findViewById(R.id.employee_room_listview);

        roomListAdapter = new MasterEmployeeRoomListAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                roomListQuery = new ParseQuery<>("RoomList");
                roomListQuery.orderByAscending("room");
                return roomListQuery;
            }
        });
        roomListAdapter.setObjectsPerPage(100);
        roomListView.setAdapter(roomListAdapter);
        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomObject = (ParseObject)parent.getItemAtPosition(position);
                if(!selectedEmployeeName.equals("")){
                    roomObject.put("current_name", selectedEmployeeName);
                    roomObject.put("clean", 0);
                    roomObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            roomListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        roomListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject clearRoomObject = (ParseObject)parent.getItemAtPosition(position);
                clearRoomObject.remove("current_name");
                clearRoomObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        roomListAdapter.notifyDataSetChanged();
                    }
                });
                return true;
            }
        });

        employeeListAdapter = new MasterEmployeeListAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseUser>() {
            @Override
            public ParseQuery<ParseUser> create() {
                employeeListQuery = new ParseQuery<>("_User");
                employeeListQuery.whereEqualTo("MasterMode", 0);
                employeeListQuery.orderByAscending("username");
                return employeeListQuery;
            }
        });
        employeeListView.setAdapter(employeeListAdapter);
        employeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                employeeUser = (ParseUser)parent.getItemAtPosition(position);
                selectedEmployeeName = employeeUser.getUsername();
            }
        });
        employeeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ParseUser deleteUser = (ParseUser)parent.getItemAtPosition(position);
                showDeleteConfirmation(deleteUser);
                return true;
            }
        });

        return view;
    }

    public void showDeleteConfirmation(final ParseUser user){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Delete Employee");
        dialogBuilder.setMessage("Are you sure you want to delete this employee?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userID = user.getObjectId();
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("userId", userID);
                ParseCloud.callFunctionInBackground("deleteUser", params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object object, ParseException e) {
                        if(e==null){
                            employeeListAdapter.loadObjects();
                        }else{
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.master_employee_list_menu, menu);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            selectedEmployeeName=savedInstanceState.getString("name");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name",selectedEmployeeName);
    }

}
