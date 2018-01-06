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

import com.milanparikh.hotelmonitor.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Owner extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    OwnerEmployeeListAdapter<ParseUser> ownerEmployeeListAdapter;
    ParseQuery<ParseUser> employeeQuery;
    OwnerDataListAdapter<ParseObject> ownerDataListAdapter;
    ParseQuery<ParseObject> employeeDataQuery;
    String employeeName = "";
    Button dateButton;
    Date selectedDate;
    Date nextDate;
    Date currentDate;
    Date past30Date;
    Date past45Date;
    Date past90Date;
    TextView avg30Text;
    TextView avg90Text;
    int avg45;
    int avg90;
    int totalavg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.owner_toolbar);
        setSupportActionBar(toolbar);

        avg30Text = (TextView)findViewById(R.id.owner_30day_avg_text);
        avg90Text = (TextView)findViewById(R.id.owner_90day_avg_text);

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        selectedDate = cal.getTime();
        currentDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        nextDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -31);
        past30Date = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -15);
        past45Date = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -45);
        past90Date = cal.getTime();

        final ListView employeeListView = (ListView) findViewById(R.id.owner_employee_listview);
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
                employeeDataQuery.whereEqualTo("username", employeeName);
                ownerDataListAdapter.loadObjects();
                ParseQuery<ParseObject> avg30Query = ParseQuery.getQuery("RoomData");
                avg30Query.orderByAscending("createdAt");
                avg30Query.whereEqualTo("username", employeeName);
                avg30Query.whereGreaterThanOrEqualTo("createdAt", past30Date);
                avg30Query.whereLessThan("createdAt", currentDate);
                avg30Query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        int count = objects.size();
                        int total = 0;
                        for (int i = 0; i<count; i++) {
                            total = total + Integer.parseInt(objects.get(i).getString("elapsed_time"));
                        }
                        int avg30;
                        if(count!=0){
                            avg30 = total/count;
                        }else{
                            avg30 = 0;
                        }
                        avg30Text.setText("30 Day Average: " + convertMilliseconds(avg30));
                    }
                });

                ParseQuery<ParseObject> avg45Query = ParseQuery.getQuery("RoomData");
                avg45Query.orderByAscending("createdAt");
                avg45Query.whereEqualTo("username", employeeName);
                avg45Query.whereGreaterThanOrEqualTo("createdAt", past45Date);
                avg45Query.whereLessThan("createdAt", currentDate);
                avg45Query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        int count = objects.size();
                        int total = 0;
                        for (int i = 0; i<count; i++) {
                            total = total + Integer.parseInt(objects.get(i).getString("elapsed_time"));
                        }
                        if(count!=0){
                            avg45 = total/count;
                        }else{
                            avg45 = 0;
                        }
                        ParseQuery<ParseObject> avg90Query = ParseQuery.getQuery("RoomData");
                        avg90Query.orderByAscending("createdAt");
                        avg90Query.whereEqualTo("username", employeeName);
                        avg90Query.whereGreaterThanOrEqualTo("createdAt", past90Date);
                        avg90Query.whereLessThan("createdAt", past45Date);
                        avg90Query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                int count = objects.size();
                                int total = 0;
                                for (int i = 0; i<count; i++) {
                                    total = total + Integer.parseInt(objects.get(i).getString("elapsed_time"));
                                }
                                if(count!=0){
                                    avg90 = total/count;
                                }else{
                                    avg90 = 0;
                                }

                                if((avg45!=0) && (avg90!=0)) {
                                    totalavg = (avg45+avg90)/2;
                                }else if(avg45!=0 && avg90==0) {
                                    totalavg = avg45;
                                }else if(avg90!=0 && avg45==0){
                                    totalavg = avg90;
                                }

                                Log.d("AVG", Integer.toString(totalavg));
                                avg90Text.setText("90 Day Average: " + convertMilliseconds(totalavg));
                            }
                        });
                    }
                });
            }
        });


        ListView employeeTimesListView =(ListView) findViewById(R.id.owner_times_listview);
        ownerDataListAdapter = new OwnerDataListAdapter<>(getApplicationContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                employeeDataQuery = ParseQuery.getQuery("RoomData");
                employeeDataQuery.orderByAscending("createdAt");
                employeeDataQuery.whereEqualTo("username", employeeName);
                employeeDataQuery.whereGreaterThanOrEqualTo("createdAt", selectedDate);
                employeeDataQuery.whereLessThan("createdAt", nextDate);
                return employeeDataQuery;
            }
        });
        employeeTimesListView.setAdapter(ownerDataListAdapter);

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
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.YEAR, year);

        selectedDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        nextDate = cal.getTime();

        employeeDataQuery.whereGreaterThanOrEqualTo("createdAt", selectedDate);
        employeeDataQuery.whereLessThan("createdAt", nextDate);
        ownerDataListAdapter.loadObjects();
    }

    public String convertMilliseconds(int ms){
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(ms),
                TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
        return hms;
    }

}

