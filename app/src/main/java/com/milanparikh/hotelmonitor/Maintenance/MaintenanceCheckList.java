package com.milanparikh.hotelmonitor.Maintenance;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.Client.ChecklistFragment;
import com.milanparikh.hotelmonitor.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class MaintenanceCheckList extends AppCompatActivity implements ChecklistFragment.OnFragmentInteractionListener{
    String objectID;
    ParseObject roomListObject;
    ProgressDialog progressDialog;
    ParseObject maintenanceListObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_check_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.maintenance_checklist_toolbar);
        toolbar.setTitle("Maintenance Checklist");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        objectID = getIntent().getExtras().getString("objectID");
        final ParseQuery<ParseObject> roomListQuery = ParseQuery.getQuery("RoomList");
        roomListQuery.getInBackground(objectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    String room = Integer.toString(object.getInt("room"));
                    toolbar.setTitle("Maintenance Checklist: " + room);
                }
            }
        });
        roomListObject = getIntent().getExtras().getParcelable("roomListObject");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        ParseQuery<ParseObject> maintenanceObjectQuery = ParseQuery.getQuery("MaintenanceList");
        maintenanceObjectQuery.whereEqualTo("room", roomListObject.getInt("room"));
        maintenanceObjectQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                maintenanceListObject = objects.get(0);
                ChecklistFragment checklistFragment = ChecklistFragment.newInstance(null, roomListObject, maintenanceListObject, "Maintenance", "maintenance");

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.maintenance_frame, checklistFragment);
                ft.commit();
                progressDialog.dismiss();

            }
        });




        TextView submitButton = (TextView)findViewById(R.id.maintenance_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomListObject.put("clean", 2);
                roomListObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            finish();
                        }
                    }
                });
            }
        });

    }

    public void onFragmentInteraction (Uri uri) {

    }
}
