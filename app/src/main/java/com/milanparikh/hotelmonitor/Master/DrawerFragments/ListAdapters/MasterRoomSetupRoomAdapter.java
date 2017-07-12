package com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by milan on 7/7/2017.
 */

public class MasterRoomSetupRoomAdapter<T extends ParseObject> extends ParseQueryAdapter {

    public MasterRoomSetupRoomAdapter(Context context, QueryFactory<T> queryFactory){
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_master_room_setup_rooms, null);
        }

        super.getItemView(object, v, parent);

        TextView roomNumber = (TextView)v.findViewById(R.id.room_number_textView);
        TextView roomAcronym = (TextView)v.findViewById(R.id.room_acronym_textView);

        roomNumber.setText(Integer.toString(object.getInt("room")));
        roomAcronym.setText(object.getString("type"));

        return v;
    }

}
