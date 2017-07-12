package com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

/**
 * Created by milan on 7/10/2017.
 */

public class MasterAvailabilityListAdapter<T extends ParseObject> extends ParseQueryAdapter {

    public MasterAvailabilityListAdapter(Context context, QueryFactory<T> queryFactory){
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_master_availability, null);
        }

        super.getItemView(object, v, parent);

        TextView typeText = (TextView)v.findViewById(R.id.availability_room_type);
        typeText.setText(object.getString("acronym"));

        final TextView countText = (TextView)v.findViewById(R.id.availability_room_count);
        ParseQuery countQuery = new ParseQuery<T>("RoomList");
        countQuery.whereEqualTo("clean", 2);
        countQuery.whereEqualTo("type", object.getString("acronym"));
        countQuery.setLimit(500);
        countQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e==null){
                    String availabilityString = Integer.toString(count) + " available";
                    countText.setText(availabilityString);
                }
            }
        });

        return v;
    }
}
