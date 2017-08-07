package com.milanparikh.hotelmonitor.Master.DrawerFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.Master.MasterExport;
import com.milanparikh.hotelmonitor.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MasterManagmentDashboard extends Fragment {
    AppCompatActivity activity;
    TextView vacantText;
    TextView dueOutText;
    TextView stayOverText;
    TextView privacyText;
    TextView maintenanceText;
    TextView outOrderText;
    int vacant;
    int dueOut;
    int stayOver;
    int privacy;
    int maintenance;
    int outOrder;

    Date outDate;
    Date currentDate;

    public MasterManagmentDashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_managment_dashboard, container, false);

        activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.master_management_dashboard_toolbar);
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        vacantText = (TextView)view.findViewById(R.id.management_vacant_text);
        dueOutText = (TextView)view.findViewById(R.id.management_due_out_text);
        stayOverText = (TextView)view.findViewById(R.id.management_stay_over_text);
        privacyText = (TextView)view.findViewById(R.id.management_private_text);
        maintenanceText = (TextView)view.findViewById(R.id.management_maintenance_text);
        outOrderText = (TextView)view.findViewById(R.id.management_out_order_text);

        ParseQuery<ParseObject> roomListQuery = ParseQuery.getQuery("RoomList");
        roomListQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (int i=0; i<objects.size(); i++){
                    ParseObject roomListObject = objects.get(i);
                    String checkInDate = roomListObject.getString("checkindate");
                    String checkOutDate = roomListObject.getString("checkoutdate");
                    int status = roomListObject.getInt("clean");
                    switch(status){
                        case 0:
                            checkGuestStatus(checkInDate, checkOutDate);
                            break;
                        case 1:
                            checkGuestStatus(checkInDate, checkOutDate);
                            break;
                        case 2:
                            checkGuestStatus(checkInDate, checkOutDate);
                            break;
                        case 3:
                            privacy++;
                            break;
                        case 4:
                            if(checkOutDate==null){
                                vacant++;
                            }
                            break;
                        case 5:
                            maintenance++;
                            break;
                        case 6:
                            outOrder++;
                            break;
                    }
                }
                vacantText.setText(Integer.toString(vacant) + " - Vacant and Checked");
                dueOutText.setText(Integer.toString(dueOut) + " - Due Out");
                stayOverText.setText(Integer.toString(stayOver) + " - Stay Over");
                privacyText.setText(Integer.toString(privacy) + " - Set Private");
                maintenanceText.setText(Integer.toString(maintenance) + " - Maintenance Required");
                outOrderText.setText(Integer.toString(outOrder) + " - Out of Order");

            }
        });

        return view;
    }

    public void checkGuestStatus(String checkInDate, String checkOutDate){
        if(checkInDate==null || checkOutDate==null) {
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            try{
                outDate = sdf.parse(checkOutDate);
                currentDate = sdf.parse(sdf.format(new Date()));
            }catch (java.text.ParseException e){
                e.printStackTrace();
            }
            if(currentDate.equals(outDate)){
                dueOut++;
            }else if(currentDate.before(outDate)){
                stayOver++;
            }else if(currentDate.after(outDate)){
            }
        }
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
                ParseUser.logOut();
                activity.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
