package com.milanparikh.hotelmonitor.Client;

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

import com.milanparikh.hotelmonitor.Client.CheckListFragments.BathroomFragment;
import com.milanparikh.hotelmonitor.Client.CheckListFragments.BedroomFragment;
import com.milanparikh.hotelmonitor.Client.CheckListFragments.ClosetFragment;
import com.milanparikh.hotelmonitor.Client.CheckListFragments.FragmentData;
import com.milanparikh.hotelmonitor.Client.CheckListFragments.MaintenanceFragment;
import com.milanparikh.hotelmonitor.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ClientCheckList extends AppCompatActivity
        implements ClosetFragment.OnFragmentInteractionListener,
        BedroomFragment.OnFragmentInteractionListener,
        BathroomFragment.OnFragmentInteractionListener,
        MaintenanceFragment.OnFragmentInteractionListener{

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
    ParseObject maintenanceObject;
    ParseUser user;
    Long startTime;
    Long endTime;
    Boolean privacy=false;
    int room;

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

        roomDataObject = new ParseObject("RoomData");

        TextView toolbarUsername = (TextView)findViewById(R.id.toolbar_username);
        toolbarUsername.setText(username);

        objectID = getIntent().getExtras().getString("objectID");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RoomList");
        query.getInBackground(objectID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e==null) {
                    roomListObject = object;
                    title = "Room Checklist: " + Integer.toString(object.getInt("room"));
                    room = object.getInt("room");
                    getSupportActionBar().setTitle(title);
                }
            }
        });

        ParseQuery<ParseObject> maintenanceQuery = ParseQuery.getQuery("Maintenance");
        query.whereEqualTo("room", room);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    maintenanceObject = objects.get(0);
                }
            }
        });

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
                if(privacy==true){
                    roomListObject.put("clean",3);
                    roomDataObject.put("privacy", 1);
                }
                else if (privacy==false){
                    roomListObject.put("clean",2);
                    roomDataObject.put("privacy", 0);
                }
                roomListObject.remove("current_name");
                roomListObject.saveInBackground();
                getClosetFragmentValues();
                getBedroomFragmentValues();
                getBathroomFragmentValues();
                getMaintenanceFragmentValues();
                getElapsedTime();
                roomDataObject.put("user", user);
                roomDataObject.put("username",username);
                roomDataObject.put("room", roomListObject.getInt("room"));
                roomDataObject.saveInBackground();
                finish();
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
            switch (position) {
                case 0:
                    return ClosetFragment.newInstance();
                case 1:
                    return BedroomFragment.newInstance();
                case 2:
                    return BathroomFragment.newInstance();
                case 3:
                    return MaintenanceFragment.newInstance();
                default:
                    return ClosetFragment.newInstance();
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

    public void getClosetFragmentValues() {
        ClosetFragment closetFragment = (ClosetFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+ R.id.container + ":" + "0");
        FragmentData.ClosetValues closetValues = closetFragment.getClosetValues();
        roomDataObject.put("closet_walls", closetValues.getClosetWallsInt());
        roomDataObject.put("closet_doors", closetValues.getClosetDoorsInt());
        roomDataObject.put("closet_iron_board", closetValues.getClosetIronBoardInt());
        roomDataObject.put("closet_shelves_rod", closetValues.getClosetShelvesRodInt());
        roomDataObject.put("closet_carpet_floor", closetValues.getClosetCarpetFloorInt());
    }

    public void getBedroomFragmentValues() {
        BedroomFragment bedroomFragment = (BedroomFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+ R.id.container + ":" + "1");
        FragmentData.BedroomValues bedroomValues = bedroomFragment.getBedroomValues();
        roomDataObject.put("bedroom_dresser", bedroomValues.getBedroomDresserInt());
        roomDataObject.put("bedroom_coffee_station", bedroomValues.getBedroomCoffeeStationInt());
        roomDataObject.put("bedroom_carpet_floor", bedroomValues.getBedroomCarpetFloorInt());
        roomDataObject.put("bedroom_windows_sills", bedroomValues.getBedroomWindowsSillsInt());
        roomDataObject.put("bedroom_blanket_linen", bedroomValues.getBedroomBlanketLinenInt());
        roomDataObject.put("bedroom_bedspread", bedroomValues.getBedroomBedspreadInt());
        roomDataObject.put("bedroom_under_bed", bedroomValues.getBedroomUnderBedInt());
        roomDataObject.put("bedroom_phones", bedroomValues.getBedroomPhonesInt());
        roomDataObject.put("bedroom_pictures_mirrors", bedroomValues.getBedroomPicturesMirrorsInt());
        roomDataObject.put("bedroom_entrance_door", bedroomValues.getBedroomEntranceDoorInt());
        roomDataObject.put("bedroom_fridge", bedroomValues.getBedroomFridgeInt());
        roomDataObject.put("bedroom_ptac", bedroomValues.getBedroomPtacInt());
        roomDataObject.put("bedroom_lamp_shades", bedroomValues.getBedroomLampShadesInt());
    }

    public void getBathroomFragmentValues() {
        BathroomFragment bathroomFragment = (BathroomFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:"+ R.id.container + ":" + "2");
        FragmentData.BathroomValues bathroomValues = bathroomFragment.getBathroomValues();
        roomDataObject.put("bathroom_light_fixtures", bathroomValues.getBathroomLightFixturesInt());
        roomDataObject.put("bathroom_vanity", bathroomValues.getBathroomVanityInt());
        roomDataObject.put("bathroom_hair_dryer", bathroomValues.getBathroomHairDryerInt());
        roomDataObject.put("bathroom_phones", bathroomValues.getBathroomPhonesInt());
        roomDataObject.put("bathroom_toilet", bathroomValues.getBathroomToiletInt());
        roomDataObject.put("bathroom_tub_walls", bathroomValues.getBathroomTubWallsInt());
        roomDataObject.put("bathroom_tub_base", bathroomValues.getBathroomTubBaseInt());
        roomDataObject.put("bathroom_shower_head", bathroomValues.getBathroomShowerHeadInt());
        roomDataObject.put("bathroom_towels", bathroomValues.getBathroomTowelsInt());
        roomDataObject.put("bathroom_floor", bathroomValues.getBathroomFloorInt());
    }

    public void getMaintenanceFragmentValues() {
        MaintenanceFragment maintenanceFragment = (MaintenanceFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:"+ R.id.container + ":" + "3");
        FragmentData.MaintenanceValues maintenanceValues = maintenanceFragment.getMaintenanceValues();
        roomDataObject.put("maintenance_toilet", maintenanceValues.getMaintenanceToiletInt());
        roomDataObject.put("maintenance_shower_drain", maintenanceValues.getMaintenanceShowerDrainInt());
        roomDataObject.put("maintenance_shower_curtain", maintenanceValues.getMaintenanceShowerCurtainInt());
        roomDataObject.put("maintenance_air_conditioning", maintenanceValues.getMaintenanceAirConditioningInt());
        roomDataObject.put("maintenance_television", maintenanceValues.getMaintenanceTelevisionInt());
        roomDataObject.put("maintenance_remote", maintenanceValues.getMaintenanceRemoteInt());
        roomDataObject.put("maintenance_window_curtain", maintenanceValues.getMaintenanceWindowCurtainInt());
        roomDataObject.put("maintenance_carpet", maintenanceValues.getMaintenanceCarpetInt());
        roomDataObject.put("maintenance_room_lock", maintenanceValues.getMaintenanceRoomLockInt());
        roomDataObject.put("maintenance_light_bulbs", maintenanceValues.getMaintenanceLightBulbsInt());
        roomDataObject.put("maintenance_thermostat", maintenanceValues.getMaintenanceThermostatInt());
        roomDataObject.put("maintenance_hair_dryer", maintenanceValues.getMaintenanceHairDryerInt());
        roomDataObject.put("maintenance_bible", maintenanceValues.getMaintenanceBibleInt());

        maintenanceObject.put("maintenance_toilet", maintenanceValues.getMaintenanceToiletInt());
        maintenanceObject.put("maintenance_shower_drain", maintenanceValues.getMaintenanceShowerDrainInt());
        maintenanceObject.put("maintenance_shower_curtain", maintenanceValues.getMaintenanceShowerCurtainInt());
        maintenanceObject.put("maintenance_air_conditioning", maintenanceValues.getMaintenanceAirConditioningInt());
        maintenanceObject.put("maintenance_television", maintenanceValues.getMaintenanceTelevisionInt());
        maintenanceObject.put("maintenance_remote", maintenanceValues.getMaintenanceRemoteInt());
        maintenanceObject.put("maintenance_window_curtain", maintenanceValues.getMaintenanceWindowCurtainInt());
        maintenanceObject.put("maintenance_carpet", maintenanceValues.getMaintenanceCarpetInt());
        maintenanceObject.put("maintenance_room_lock", maintenanceValues.getMaintenanceRoomLockInt());
        maintenanceObject.put("maintenance_light_bulbs", maintenanceValues.getMaintenanceLightBulbsInt());
        maintenanceObject.put("maintenance_thermostat", maintenanceValues.getMaintenanceThermostatInt());
        maintenanceObject.put("maintenance_hair_dryer", maintenanceValues.getMaintenanceHairDryerInt());
        maintenanceObject.put("maintenance_bible", maintenanceValues.getMaintenanceBibleInt());
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
        roomListObject.put("clean", 0);
        roomListObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
    }

}
