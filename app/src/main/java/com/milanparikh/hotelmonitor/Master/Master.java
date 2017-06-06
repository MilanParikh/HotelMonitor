package com.milanparikh.hotelmonitor.Master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class Master extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        Toolbar toolbar = (Toolbar) findViewById(R.id.master_toolbar);
        setSupportActionBar(toolbar);

        ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery query = new ParseQuery("RoomList");
                query.orderByDescending("room");
                return query;
            }
        });
        adapter.setTextKey("room");
        adapter.setObjectsPerPage(60);

        ListView listView = (ListView) findViewById(R.id.master_room_list);
        listView.setAdapter(adapter);
    }
}
