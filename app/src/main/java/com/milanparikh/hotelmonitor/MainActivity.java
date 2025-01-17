package com.milanparikh.hotelmonitor;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.milanparikh.hotelmonitor.Client.Client;
import com.milanparikh.hotelmonitor.Maintenance.Maintenance;
import com.milanparikh.hotelmonitor.Master.Master;
import com.milanparikh.hotelmonitor.Other.DownloadUpdate;
import com.milanparikh.hotelmonitor.Other.SettingsActivity;
import com.milanparikh.hotelmonitor.Owner.Owner;
import com.parse.ConfigCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;
    String serverURL;
    String appID;
    URL serverURLObject;
    String adminPassword;
    String offlinePassword;
    EditText usernameEditText;
    EditText passwordEditText;
    public ParseUser user;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    DevicePolicyManager devicePM;
    Boolean kioskModeAvailable;
    Boolean correctPassword;
    MenuItem kioskItem;
    Button loginButton;
    Button registerButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ComponentName deviceAdmin = new ComponentName(this, AdminReceiver.class);
        devicePM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!devicePM.isAdminActive(deviceAdmin)) {
            //Toast.makeText(this, "Not Device Admin", Toast.LENGTH_SHORT).show();
            kioskModeAvailable=false;
        }

        if (devicePM.isDeviceOwnerApp(getPackageName())) {
            devicePM.setLockTaskPackages(deviceAdmin, new String[]{getPackageName()});
            kioskModeAvailable=true;
        } else {
            //Toast.makeText(this, "Not Device Owner", Toast.LENGTH_SHORT).show();
            kioskModeAvailable=false;
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefEditor = sharedPref.edit();

        if(kioskModeAvailable && sharedPref.getBoolean("kiosk_mode", true)){
            toggleKiosk(true);
        }

        serverURL = sharedPref.getString("server_preference","");
        appID = sharedPref.getString("app_id_preference","");
        try{
            serverURLObject = new URL(sharedPref.getString("server_preference",""));
            serverURL = serverURLObject.toString();
        }catch (MalformedURLException e){
            Log.d("ParseServerURL", e.toString());
            serverURL = "";
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage("Invalid Server URL, please contact administrator");
            dialog.setTitle("Unable to Initialize");
            dialog.show();
        }

        if(!serverURL.equals("")){
            Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                    .applicationId(appID)
                    .server(serverURL)
                    .build()
            );
            ParseInstallation.getCurrentInstallation().saveInBackground();

        }


        usernameEditText = (EditText)findViewById(R.id.username_edittext);
        passwordEditText = (EditText)findViewById(R.id.password_edittext);

        loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser pUser, ParseException e) {
                        if (pUser != null) {
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", pUser);
                            installation.saveInBackground();
                            Toast.makeText(MainActivity.this, pUser.getUsername()+ " successfully logged In", Toast.LENGTH_SHORT).show();
                            int masterMode = pUser.getInt("MasterMode");
                            switch(masterMode){
                                case 0:
                                    Intent launchClient = new Intent(getApplicationContext(), Client.class);
                                    startActivity(launchClient);
                                    break;
                                case 1:
                                    Intent launchMaster = new Intent(getApplicationContext(), Master.class);
                                    startActivity(launchMaster);
                                    break;
                                case 2:
                                    Intent launchMaint = new Intent(getApplicationContext(), Maintenance.class);
                                    startActivity(launchMaint);
                                    break;
                                /*case 3:
                                    Intent launchOwner = new Intent(getApplicationContext(), Owner.class);
                                    startActivity(launchOwner);
                                    break;*/
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Log in failed: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        registerButton = (Button)findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(registerIntent);
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
        menu.findItem(R.id.action_kiosk).setEnabled(kioskModeAvailable);
        menu.findItem(R.id.action_disable_admin).setEnabled(kioskModeAvailable);
        menu.findItem(R.id.action_kiosk).setChecked(sharedPref.getBoolean("kiosk_mode",true));
        kioskItem = menu.findItem(R.id.action_kiosk);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                checkOfflinePassword("settings");
                return true;
            case R.id.action_update:
                checkOfflinePassword("update");
                return true;
            case R.id.action_kiosk:
                //checkPassword("kiosk");
                if(sharedPref.getBoolean("kiosk_mode", false)){
                    toggleKiosk(false);
                }else{
                    toggleKiosk(true);
                }
                return true;
            case R.id.action_disable_admin:
                checkOfflinePassword("admin");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleKiosk(boolean enabled) {
        try {
            if (enabled) {
                if (devicePM.isLockTaskPermitted(this.getPackageName())) {
                    startLockTask();
                    prefEditor.putBoolean("kiosk_mode",true);
                    prefEditor.commit();
                    kioskItem.setChecked(true);
                } else {
                    Toast.makeText(this, "Kiosk mode not allowed", Toast.LENGTH_SHORT).show();
                }
            } else {
                stopLockTask();
                prefEditor.putBoolean("kiosk_mode",false);
                prefEditor.commit();
                kioskItem.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPassword(final String scenario) {
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                if(e==null){
                }else {
                    config = ParseConfig.getCurrentConfig();
                }
                adminPassword = config.getString("admin_password");
            }
        });
        final AlertDialog.Builder passwordBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_password, null);
        passwordBuilder.setView(dialogView);

        final EditText dialogPasswordEditText = (EditText)dialogView.findViewById(R.id.dialog_password_edittext);

        passwordBuilder.setTitle("Enter Password");
        passwordBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String submittedPassword = dialogPasswordEditText.getText().toString();
                correctPassword = (adminPassword.equals(submittedPassword));
                if(correctPassword){
                    optionsActions(scenario);
                }
            }
        });
        passwordBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog passwordDialog = passwordBuilder.create();
        passwordDialog.show();
        final Button posButton = passwordDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        dialogPasswordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)) {
                    posButton.performClick();
                }

                return false;
            }
        });
    }

    public void checkOfflinePassword(final String scenario) {
        offlinePassword = getString(R.string.admin_password);
        final AlertDialog.Builder passwordBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_password, null);
        passwordBuilder.setView(dialogView);

        final EditText dialogPasswordEditText = (EditText)dialogView.findViewById(R.id.dialog_password_edittext);

        passwordBuilder.setTitle("Enter Password");
        passwordBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String submittedPassword = dialogPasswordEditText.getText().toString();
                correctPassword = (offlinePassword.equals(submittedPassword));
                if(correctPassword){
                    optionsActions(scenario);
                }
            }
        });
        passwordBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog passwordDialog = passwordBuilder.create();
        passwordDialog.show();
        final Button posButton = passwordDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        dialogPasswordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)) {
                    posButton.performClick();
                }

                return false;
            }
        });
    }

    public void optionsActions(String scenario){
        switch(scenario) {
            case "update":
                toggleKiosk(false);
                correctPassword = false;
                updateFunction();
                break;
            case "kiosk":
                toggleKiosk(!sharedPref.getBoolean("kiosk_mode",false));
                kioskItem.setChecked(sharedPref.getBoolean("kiosk_mode",false));
                correctPassword = false;
                break;
            case "admin":
                try {
                    devicePM.clearDeviceOwnerApp("com.milanparikh.hotelmonitor");
                }catch(SecurityException e) {
                    e.printStackTrace();
                    Log.d("TAG","Already disabled");
                }
                break;
            case "settings":
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }
    }

    public void updateFunction() {
        verifyStoragePermissions(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading Update");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        final DownloadUpdate downloadUpdate = new DownloadUpdate(getApplicationContext(), progressDialog);
        downloadUpdate.execute();

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadUpdate.cancel(true);
            }
        });

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onResume() {
        passwordEditText.setText("");
        super.onResume();
    }
}
