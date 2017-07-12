package com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by milan on 7/3/2017.
 */

public class MasterRoomSetupFloorAdapter<T extends ParseObject> extends ParseQueryAdapter {

    public MasterRoomSetupFloorAdapter(Context context, QueryFactory<T> queryFactory){
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_employee_list, null);
        }

        TextView floor = (TextView)v.findViewById(R.id.employee_name);
        String floorText = "Floor " + Integer.toString(object.getInt("floor"));
        floor.setText(floorText);

        super.getItemView(object, v, parent);

        return v;
    }

}
