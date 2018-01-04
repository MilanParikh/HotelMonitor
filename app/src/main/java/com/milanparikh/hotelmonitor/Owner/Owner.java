package com.milanparikh.hotelmonitor.Owner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.GregorianCalendar;
import java.util.List;

public class Owner extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    OwnerEmployeeListAdapter<ParseUser> ownerEmployeeListAdapter;
    ParseQuery<ParseUser> employeeQuery;
    OwnerDataListAdapter<ParseObject> ownerDataListAdapter;
    ParseQuery<ParseObject> employeeDataQuery;
    String employeeName = "";
    String createdAt;
    Button dateButton;

    ParseQuery<ParseObject> roomDataQuery;

    Date today;
    Date tomorrow;

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

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        today = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow = cal.getTime();



        ListView employeeTimesListView =(ListView) findViewById(R.id.owner_times_listview);
        ownerDataListAdapter = new OwnerDataListAdapter<>(getApplicationContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                employeeDataQuery = ParseQuery.getQuery("RoomData");
                employeeDataQuery.orderByAscending("createdAt");
                employeeDataQuery.whereEqualTo("username", "mp2");
                employeeDataQuery.whereGreaterThanOrEqualTo("createdAt", today);
                employeeDataQuery.whereLessThan("createdAt", tomorrow);
                return employeeDataQuery;
            }
        });
        employeeTimesListView.setAdapter(ownerDataListAdapter);

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

        dateButton = (Button) findViewById(R.id.owner_date_selector_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });


    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), (Owner)getActivity(), year, month, day);
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        dateButton.setText(month+1 + "/" + day +"/" + year);
    }

}

