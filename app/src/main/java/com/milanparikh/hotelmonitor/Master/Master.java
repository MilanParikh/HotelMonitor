package com.milanparikh.hotelmonitor.Master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.milanparikh.hotelmonitor.R;
import com.parse.Parse;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SubscriptionHandling;

public class Master extends AppCompatActivity {
    ParseQuery query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        Toolbar toolbar = (Toolbar) findViewById(R.id.master_toolbar);
        setSupportActionBar(toolbar);

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        final ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                query = new ParseQuery("RoomList");
                query.orderByDescending("room");
                return query;
            }
        });

        SubscriptionHandling<ParseObject> subscriptionHandling = parseLiveQueryClient.subscribe(query);
        subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
            @Override
            public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                adapter.loadObjects();
            }
        });

        adapter.setTextKey("room");
        adapter.setObjectsPerPage(60);

        ListView listView = (ListView) findViewById(R.id.master_room_list);
        listView.setAdapter(adapter);
    }
}
