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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters.MasterRoomSetupFloorAdapter;
import com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters.MasterRoomSetupRoomAdapter;
import com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters.MasterRoomSetupTypeAdapter;
import com.milanparikh.hotelmonitor.Master.MasterExport;
import com.milanparikh.hotelmonitor.Other.SettingsActivity;
import com.milanparikh.hotelmonitor.R;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class MasterRoomSetup extends Fragment {
    AppCompatActivity activity;
    MasterRoomSetupFloorAdapter<ParseObject> floorAdapter;
    ParseQuery<ParseObject> floorQuery;
    int selectedFloor;
    MasterRoomSetupTypeAdapter<ParseObject> typeAdapter;
    ParseQuery<ParseObject> typeQuery;
    String selectedType="";
    MasterRoomSetupRoomAdapter<ParseObject> roomAdapter;
    ParseQuery<ParseObject> roomQuery;


    public MasterRoomSetup() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_room_setup, container, false);

        activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.master_room_setup_toolbar);
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ListView setupRoomList = (ListView)view.findViewById(R.id.setup_room_listview);
        ListView setupFloorList = (ListView)view.findViewById(R.id.setup_floor_listview);
        ListView setupAcronymList = (ListView)view.findViewById(R.id.setup_acronym_listview);

        floorAdapter = new MasterRoomSetupFloorAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                floorQuery = new ParseQuery<>("Floors");
                floorQuery.orderByAscending("floor");
                return floorQuery;
            }
        });
        setupFloorList.setAdapter(floorAdapter);
        setupFloorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject selectedFloorObject = (ParseObject)parent.getItemAtPosition(position);
                selectedFloor = selectedFloorObject.getInt("floor");
                roomQuery.whereEqualTo("floor", selectedFloor);
                roomAdapter.loadObjects();
            }
        });

        typeAdapter = new MasterRoomSetupTypeAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                typeQuery = new ParseQuery<>("RoomTypes");
                typeQuery.orderByAscending("acronym");
                return typeQuery;
            }
        });
        setupAcronymList.setAdapter(typeAdapter);
        setupAcronymList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject selectedTypeObject = (ParseObject)parent.getItemAtPosition(position);
                selectedType = selectedTypeObject.getString("acronym");
            }
        });

        roomAdapter = new MasterRoomSetupRoomAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                roomQuery = new ParseQuery<>("RoomList");
                roomQuery.orderByAscending("room");
                return roomQuery;
            }
        });
        roomAdapter.setObjectsPerPage(100);
        setupRoomList.setAdapter(roomAdapter);
        setupRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject selectedRoomObject = (ParseObject)parent.getItemAtPosition(position);
                if(!selectedType.equals("")) {
                    selectedRoomObject.put("type", selectedType);
                    selectedRoomObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            roomAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        });
        setupRoomList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject selectedRoomObject = (ParseObject)parent.getItemAtPosition(position);
                showDeleteConfirmation(selectedRoomObject);
                return true;
            }
        });

        final TextView addFloorButton = (TextView)view.findViewById(R.id.add_floor_textView);
        addFloorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFloor();
            }
        });

        return view;
    }

    public void addFloor(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_floor, null);
        builder.setView(dialogView);

        final EditText floorEditText = (EditText)dialogView.findViewById(R.id.floor_editText);
        final EditText startingEditText = (EditText)dialogView.findViewById(R.id.starting_editText);
        final EditText numberRoomsEditText = (EditText)dialogView.findViewById(R.id.number_rooms_editText);

        builder.setTitle("Add Floor");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseObject floorObject = new ParseObject("Floors");
                floorObject.put("floor", Integer.parseInt(floorEditText.getText().toString()));
                floorObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        floorAdapter.loadObjects();
                    }
                });

                int startingNumber = Integer.parseInt(startingEditText.getText().toString());
                int numberRooms = Integer.parseInt(numberRoomsEditText.getText().toString());
                int endingNumber = startingNumber+numberRooms;

                for(int i=startingNumber; i<endingNumber; i++){
                    ParseObject roomObject = new ParseObject("RoomList");
                    roomObject.put("room", i);
                    roomObject.put("floor", floorObject.getInt("floor"));
                    roomObject.put("clean", 2);
                    roomObject.put("type", "N/A");
                    roomObject.saveInBackground();
                    ParseObject maintenanceObject = new ParseObject("MaintenanceList");
                    maintenanceObject.put("room", i);
                    maintenanceObject.put("floor", floorObject.getInt("floor"));
                    maintenanceObject.saveInBackground();
                }


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDeleteConfirmation(final ParseObject object){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Delete Room");
        dialogBuilder.setMessage("Are you sure you want to delete this room?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                object.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        roomAdapter.loadObjects();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            selectedFloor=savedInstanceState.getInt("floor");
            selectedType=savedInstanceState.getString("type");
            roomQuery.whereEqualTo("floor", selectedFloor);
            roomAdapter.loadObjects();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("floor", selectedFloor);
        outState.putString("type", selectedType);
    }

}
