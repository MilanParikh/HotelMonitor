package com.milanparikh.hotelmonitor.Client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseUser;

public class Client extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
    }

    @Override
    public void onBackPressed() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user!=null) {
            user.logOutInBackground();
        }
        finish();
    }
}
