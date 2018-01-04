package com.milanparikh.hotelmonitor.Master;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.Master.DrawerFragments.MasterEmployeeList;
import com.milanparikh.hotelmonitor.Master.DrawerFragments.MasterManagmentDashboard;
import com.milanparikh.hotelmonitor.Master.DrawerFragments.MasterRoomList;
import com.milanparikh.hotelmonitor.Master.DrawerFragments.MasterRoomSetup;
import com.milanparikh.hotelmonitor.Master.DrawerFragments.MasterRoomTypes;
import com.milanparikh.hotelmonitor.R;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class Master extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment currentFragment;
    String fragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_master);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_dashboard);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(savedInstanceState!=null){
            fragmentTag = savedInstanceState.getString("fragment_tag");
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, fragmentTag);
            fragmentTransaction.replace(R.id.master_container, currentFragment, fragmentTag);
            fragmentTransaction.commit();
        }else{
            currentFragment = new MasterRoomList();
            fragmentTransaction.replace(R.id.master_container, currentFragment, "MasterRoomList");
            fragmentTransaction.commit();
        }

        ParseUser user = ParseUser.getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = (TextView)headerView.findViewById(R.id.nav_user_text);
        String username = user.getUsername();
        usernameText.setText(username);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ParseUser user = ParseUser.getCurrentUser();
            if (user!=null) {
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.remove("user");
                installation.saveInBackground();
                user.logOutInBackground();
            }
            finish();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            currentFragment = new MasterRoomList();
            fragmentTag = "MasterRoomList";
        } else if (id == R.id.nav_employee_list) {
            currentFragment = new MasterEmployeeList();
            fragmentTag = "MasterEmployeeList";
        } else if (id == R.id.nav_room_types) {
            currentFragment = new MasterRoomTypes();
            fragmentTag = "MasterRoomType";
        } else if (id == R.id.nav_room_setup) {
            currentFragment = new MasterRoomSetup();
            fragmentTag = "MasterRoomSetup";
        } else if (id == R.id.nav_management_dashboard) {
            currentFragment = new MasterManagmentDashboard();
            fragmentTag = "MasterManagementDashboard";
        }

        if(currentFragment!=null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.master_container, currentFragment,fragmentTag);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("fragment_tag", fragmentTag);
        getSupportFragmentManager().putFragment(outState, fragmentTag, currentFragment);
    }
}
