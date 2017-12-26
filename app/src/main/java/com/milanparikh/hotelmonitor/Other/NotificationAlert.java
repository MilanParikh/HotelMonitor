package com.milanparikh.hotelmonitor.Other;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.milanparikh.hotelmonitor.R;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationAlert extends AppCompatActivity {
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().hide();
        setContentView(R.layout.activity_notification_alert);

        Bundle extras = getIntent().getExtras();
        String jsonString = extras.getString("com.parse.Data");
        JSONObject jsonObject;
        try{
            if(jsonString!=null && !jsonString.equals("")){
                jsonObject = new JSONObject(jsonString);
                message = jsonObject.getString("alert");

            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Message");
        builder.setMessage(message);
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }
}
