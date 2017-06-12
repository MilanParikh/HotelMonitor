package com.milanparikh.hotelmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.milanparikh.hotelmonitor.Client.Client;
import com.milanparikh.hotelmonitor.Master.Master;
import com.milanparikh.hotelmonitor.Master.MasterSetup;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    String serverURL;
    String appID;
    EditText usernameEditText;
    EditText passwordEditText;
    public ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        usernameEditText = (EditText)findViewById(R.id.username_edittext);
        passwordEditText = (EditText)findViewById(R.id.password_edittext);

        final Button loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                serverURL = sharedPref.getString("server_preference","");
                appID = sharedPref.getString("app_id_preference","");

                Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                        .applicationId(appID)
                        .server(serverURL)
                        .build()
                );
                ParseUser cUser = ParseUser.getCurrentUser();
                if (cUser!=null) {
                    cUser.logOutInBackground();
                }

                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser pUser, ParseException e) {
                        if (pUser != null) {
                            Toast.makeText(MainActivity.this, pUser.getUsername()+ " successfully logged In", Toast.LENGTH_SHORT).show();
                            int masterMode = pUser.getInt("MasterMode");
                            if (masterMode==1) {
                                Intent launchMaster = new Intent(getApplicationContext(), Master.class);
                                startActivity(launchMaster);
                            }
                            else {
                                Intent launchClient = new Intent(getApplicationContext(), Client.class);
                                startActivity(launchClient);
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Log in failed: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        Button registerbutton = (Button)findViewById(R.id.register_button);
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                serverURL = sharedPref.getString("server_preference","");
                appID = sharedPref.getString("app_id_preference","");

                Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                        .applicationId(appID)
                        .server(serverURL)
                        .build()
                );
                ParseUser cUser = ParseUser.getCurrentUser();
                if (cUser!=null) {
                    cUser.logOutInBackground();
                }
                user = new ParseUser();
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());
                user.put("MasterMode", 0);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null) {
                            Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            int masterMode = user.getInt("MasterMode");
                            if (masterMode==1) {
                                Intent launchMaster = new Intent(getApplicationContext(), Master.class);
                                startActivity(launchMaster);
                            }
                            else {
                                Intent launchClient = new Intent(getApplicationContext(), Client.class);
                                startActivity(launchClient);
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Registration failed: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)) {
                    loginButton.performClick();
                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        passwordEditText.setText("");
        super.onResume();
    }
}
