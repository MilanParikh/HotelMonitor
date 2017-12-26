package com.milanparikh.hotelmonitor;

import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegistrationActivity extends AppCompatActivity {
    TextView usernameText;
    String first="";
    String last="";
    String passwordText;
    String confirmPasswordText;
    TextInputLayout confirmWrapper;
    String username;
    String adminPassword;
    Boolean correctPassword;
    Switch masterSwitch;
    Button registerButton;
    String offlinePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = (Toolbar)findViewById(R.id.registration_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        usernameText = (TextView)findViewById(R.id.username_textView);
        registerButton = (Button)findViewById(R.id.registration_button);

        if(savedInstanceState!=null){
            registerButton.setEnabled(false);
        }

        final EditText firstName = (EditText)findViewById(R.id.first_name_editText);
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                first = firstName.getText().toString();
                setUsernameText();
            }
        });
        final EditText lastName = (EditText)findViewById(R.id.last_name_editText);
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                last = lastName.getText().toString();
                setUsernameText();
            }
        });

        final EditText password = (EditText)findViewById(R.id.password_editText);
        final EditText confirmPassword = (EditText)findViewById(R.id.confirm_password_editText);
        confirmWrapper = (TextInputLayout)findViewById(R.id.confirm_password_input_layout);
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordText = password.getText().toString();
                confirmPasswordText = confirmPassword.getText().toString();
                if(passwordText.equals(confirmPasswordText)){
                    confirmWrapper.setErrorEnabled(false);
                    registerButton.setEnabled(true);
                }else {
                    confirmWrapper.setErrorEnabled(true);
                    confirmWrapper.setError("Passwords do not match");
                    registerButton.setEnabled(false);
                }
            }
        });

        masterSwitch = (Switch)findViewById(R.id.master_mode_switch);
        masterSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOfflinePassword();
            }
        });

        final Switch maintSwitch = (Switch)findViewById(R.id.maintenance_switch);
        maintSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maintSwitch.isChecked()){
                    masterSwitch.setChecked(false);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(confirmPasswordText);
                if(masterSwitch.isChecked()){
                    user.put("MasterMode", 1);
                }else if(maintSwitch.isChecked()){
                    user.put("MasterMode", 2);
                }else{
                    user.put("MasterMode", 0);
                }

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null) {
                            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration failed: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public void setUsernameText() {
        usernameText.setText(getResources().getString(R.string.username_label) + first.toLowerCase() + last.toLowerCase());
        username = first.toLowerCase() + last.toLowerCase();
    }

    public void checkPassword() {
        masterSwitch.setChecked(false);
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
                    masterSwitch.setChecked(true);
                }else{
                    masterSwitch.setChecked(false);
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

    public void checkOfflinePassword() {
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
                    masterSwitch.setChecked(true);
                }else{
                    masterSwitch.setChecked(false);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("register_button", registerButton.isEnabled());
    }
}
