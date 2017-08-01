package com.milanparikh.hotelmonitor.Client;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientCheckList extends AppCompatActivity
        implements ChecklistFragment.OnFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    String source;
    String objectID;
    String title;
    ParseObject roomListObject;
    String username;
    TextView backButton;
    TextView nextButton;
    TextView submitButton;
    TextView privacyButton;
    ProgressBar progressBar;
    ParseObject roomDataObject;
    ParseUser user;
    Long startTime;
    Long endTime;
    Boolean privacy=false;
    int room;

    Date outDate;
    Date currentDate;
    String status;

    ChecklistFragment maintenanceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_check_list);
        startTime = SystemClock.elapsedRealtime();
        Toolbar toolbar = (Toolbar) findViewById(R.id.checklist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = ParseUser.getCurrentUser();
        username = user.getUsername();

        source = getIntent().getExtras().getString("source");

        roomDataObject = new ParseObject("RoomData");
        if(source.equals("client")) {
            roomDataObject.saveInBackground();
        }

        TextView toolbarUsername = (TextView)findViewById(R.id.toolbar_username);
        toolbarUsername.setText(username);

        objectID = getIntent().getExtras().getString("objectID");
        roomListObject = getIntent().getExtras().getParcelable("roomListObject");
        title = "Room Checklist: " + Integer.toString(roomListObject.getInt("room"));
        room = roomListObject.getInt("room");
        getSupportActionBar().setTitle(title);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        nextButton = (TextView) findViewById(R.id.bottom_next_button);
        backButton = (TextView) findViewById(R.id.bottom_back_button);
        submitButton = (TextView) findViewById(R.id.bottom_submit_button);
        privacyButton = (TextView)findViewById(R.id.bottom_private_button);
        progressBar = (ProgressBar)findViewById(R.id.checklist_progress_bar);
        progressBar.setProgress(25);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setProgressBar(mViewPager);
                switch (position) {
                    case 0:
                        backButton.setVisibility(View.GONE);
                        nextButton.setVisibility(View.VISIBLE);
                        submitButton.setVisibility(View.GONE);
                        privacyButton.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        backButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        submitButton.setVisibility(View.GONE);
                        privacyButton.setVisibility(View.GONE);
                        break;
                    case 2:
                        backButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        submitButton.setVisibility(View.GONE);
                        privacyButton.setVisibility(View.GONE);
                        break;
                    case 3:
                        backButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.GONE);
                        submitButton.setVisibility(View.VISIBLE);
                        privacyButton.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                setProgressBar(mViewPager);
                switch (position) {
                    case 0:
                        backButton.setVisibility(View.GONE);
                        nextButton.setVisibility(View.VISIBLE);
                        submitButton.setVisibility(View.GONE);
                        privacyButton.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        backButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        submitButton.setVisibility(View.GONE);
                        privacyButton.setVisibility(View.GONE);
                        break;
                    case 2:
                        backButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        submitButton.setVisibility(View.GONE);
                        privacyButton.setVisibility(View.GONE);
                        break;
                    case 3:
                        backButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.GONE);
                        submitButton.setVisibility(View.VISIBLE);
                        privacyButton.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFragment(mViewPager);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousFragment(mViewPager);
            }
        });
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(privacy==false){
                    privacy = true;
                    privacyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                }
                else if (privacy) {
                    privacy = false;
                    privacyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String checkin = roomListObject.getString("checkindate");
                String checkout = roomListObject.getString("checkoutdate");

                if(checkin==null || checkout==null) {
                    status = getString(R.string.due_out);
                }else{
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    try{
                        outDate = sdf.parse(checkout);
                        currentDate = sdf.parse(sdf.format(new Date()));
                    }catch (java.text.ParseException e){
                        e.printStackTrace();
                    }
                    if(currentDate.equals(outDate)){
                        status=getString(R.string.due_out);
                    }else if(currentDate.before(outDate)){
                        status = getString(R.string.stay_over);
                    }else if(currentDate.after(outDate)){
                        status = getString(R.string.overstayed);
                    }
                }

                if(source.equals("client")){
                    if(privacy){
                        roomListObject.put("clean",3);
                        roomDataObject.put("privacy", 1);
                    }
                    else if (!privacy){
                        roomListObject.put("clean",maintenanceFragment.getClean());
                        roomDataObject.put("privacy", 0);
                    }
                    roomListObject.remove("current_name");
                    roomListObject.saveInBackground();
                    getElapsedTime();
                    roomDataObject.put("user", user);
                    roomDataObject.put("username",username);
                    roomDataObject.put("room", roomListObject.getInt("room"));
                    roomDataObject.put("status", status);
                    roomDataObject.saveInBackground();
                    finish();
                }else if(source.equals("master")){
                    if(privacy){
                        roomListObject.put("clean",3);
                    }
                    else if (!privacy){
                        roomListObject.put("clean",4);
                    }
                    roomListObject.saveInBackground();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }


            }
        });



    }

    public void onFragmentInteraction (Uri uri) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_check_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            maintenanceFragment = ChecklistFragment.newInstance(roomDataObject.getObjectId(), roomDataObject, objectID, roomListObject, "Maintenance", source);
            switch (position) {
                case 0:
                    return ChecklistFragment.newInstance(roomDataObject.getObjectId(), roomDataObject, null, roomListObject, "Closet", source);
                case 1:
                    return ChecklistFragment.newInstance(roomDataObject.getObjectId(), roomDataObject, null, roomListObject, "Bedroom", source);
                case 2:
                    return ChecklistFragment.newInstance(roomDataObject.getObjectId(), roomDataObject, null, roomListObject, "Bathroom", source);
                case 3:
                    return maintenanceFragment;
                default:
                    return ChecklistFragment.newInstance(roomDataObject.getObjectId(), roomDataObject, null, roomListObject, "Closet", source);
            }

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Closet";
                case 1:
                    return "Bedroom";
                case 2:
                    return "Bathroom";
                case 3:
                    return "Maintenance";
            }
            return null;
        }
    }

    public void getElapsedTime() {
        endTime = SystemClock.elapsedRealtime();
        String elapsedTime = Long.toString(endTime-startTime);
        roomDataObject.put("elapsed_time", elapsedTime);
    }

    public void nextFragment(ViewPager viewPager) {
        viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
    }

    public void previousFragment(ViewPager viewPager) {
        viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
    }

    public void setProgressBar(ViewPager viewPager) {
        int progressValue = (viewPager.getCurrentItem()+1)*25;
        progressBar.setProgress(progressValue);
    }

    @Override
    public void onBackPressed() {
        roomDataObject.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(source.equals("client")){
                    roomListObject.put("clean", 0);
                    roomListObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            finish();
                        }
                    });
                }else{
                    finish();
                }

            }
        });
    }

}
