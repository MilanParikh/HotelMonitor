package com.milanparikh.hotelmonitor.Master;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.milanparikh.hotelmonitor.R;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class MasterExport extends AppCompatActivity {
    String serverURL;
    JSONArray array;
    String csv;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_export);

        final RequestQueue queue = Volley.newRequestQueue(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        serverURL = sharedPref.getString("server_preference","") + "/classes/RoomData";
        verifyStoragePermissions(this);
        Button exportButton = (Button)findViewById(R.id.export_button);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest jsObjRequest  = new JsonObjectRequest(Request.Method.GET, serverURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        js2csv(response);
                        Log.d("TAG", "Response:" + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("X-Parse-Application-Id", "HotelMonitor");
                        return params;
                    }
                };
                queue.add(jsObjRequest);
            }
        });

    }

    public void js2csv(JSONObject jsObj) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "RoomData.csv");
        try {
            array = jsObj.getJSONArray("results");
            csv = CDL.toString(array);

            ParseFile parseFile = new ParseFile("RoomData.csv", csv.getBytes());
            parseFile.saveInBackground();
            ParseObject parseObject = new ParseObject("CSVfiles");
            parseObject.put("RoomDataCSV",parseFile);
            parseObject.saveInBackground();

            if (file != null) {
                file.createNewFile();
            }
            FileUtils.writeStringToFile(file, csv, (Charset)null);

            /*FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter outWriter = new OutputStreamWriter(fOut);
            outWriter.append(csv);
            outWriter.close();
            fOut.flush();
            fOut.close();*/

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

}
