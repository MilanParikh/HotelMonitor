package com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by milan on 7/25/2017.
 */

public class MasterAvailabilityRoomListAdapter<T extends ParseObject> extends ParseQueryAdapter {

    public MasterAvailabilityRoomListAdapter(Context context, QueryFactory<T> queryFactory){
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_availability_room_list, null);
        }

        super.getItemView(object, v, parent);

        TextView roomText = (TextView)v.findViewById(R.id.availability_room_number_text);
        roomText.setText(Integer.toString(object.getInt("room")));

        return v;
    }

}
