package com.milanparikh.hotelmonitor.Maintenance;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by milan on 7/11/2017.
 */

public class MaintenanceRoomListAdapter<T extends ParseObject> extends ParseQueryAdapter {

    public MaintenanceRoomListAdapter(Context context, QueryFactory<T> queryFactory){
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_maintenance_roomlist, null);
        }
        super.getItemView(object, v, parent);

        TextView roomNum = (TextView)v.findViewById(R.id.maintenance_room_num_text);
        roomNum.setText(Integer.toString(object.getInt("room")));

        int status = object.getInt("clean");
        if(status==6){
            v.setBackgroundColor(getContext().getColor(R.color.roomListRed));
        }

        return v;
    }

}
