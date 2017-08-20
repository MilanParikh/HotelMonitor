package com.milanparikh.hotelmonitor.Owner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.milanparikh.hotelmonitor.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Owner extends AppCompatActivity {
    OwnerEmployeeListAdapter<ParseUser> ownerEmployeeListAdapter;
    ParseQuery<ParseUser> employeeQuery;
    String employeeName;
    String createdAt;

    ParseQuery<ParseObject> roomDataQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.owner_toolbar);
        setSupportActionBar(toolbar);

        ListView employeeListView = (ListView) findViewById(R.id.owner_employee_listview);
        ownerEmployeeListAdapter = new OwnerEmployeeListAdapter<>(getApplicationContext(), new ParseQueryAdapter.QueryFactory<ParseUser>() {
            @Override
            public ParseQuery<ParseUser> create() {
                employeeQuery = ParseQuery.getQuery("_User");
                employeeQuery.orderByAscending("username");
                employeeQuery.whereEqualTo("MasterMode",0);
                return employeeQuery;
            }
        });
        employeeListView.setAdapter(ownerEmployeeListAdapter);
        employeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseUser employeeUser = (ParseUser)parent.getItemAtPosition(position);
                employeeName = employeeUser.getUsername();
            }
        });

        roomDataQuery = ParseQuery.getQuery("RoomData");
        roomDataQuery.orderByDescending("createdAt");
        roomDataQuery.setLimit(1000);
        roomDataQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                ParseObject roomDataObject = (ParseObject)objects.get(0);
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                createdAt = df.format(roomDataObject.getCreatedAt());
                Calendar cal = Calendar.getInstance();
                cal.setTime(roomDataObject.getCreatedAt());
                cal.add(Calendar.DATE, -30);
                Date newDate = cal.getTime();
                String newDateString = df.format(newDate);
                TextView textView = (TextView)findViewById(R.id.owner_30day_avg_text);
                textView.setText(newDateString);
                Button button = (Button)findViewById(R.id.owner_date_selector_button);
                button.setText(createdAt);
            }
        });

    }
}
