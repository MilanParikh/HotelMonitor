package com.milanparikh.hotelmonitor.Owner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by parik on 1/4/2018.
 */

public class OwnerDataListAdapter<T extends ParseObject> extends ParseQueryAdapter {
    public OwnerDataListAdapter(Context context, QueryFactory<T> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_owner_employee_data, null);
        }

        TextView roomText = (TextView)v.findViewById(R.id.owner_employee_data_room);
        roomText.setText(Integer.toString(object.getInt("room")));
        TextView timeText = (TextView)v.findViewById(R.id.owner_employee_data_elapsedtime);
        timeText.setText(object.getString("elapsed_time"));
        TextView statusText = (TextView)v.findViewById(R.id.owner_employee_data_status);
        statusText.setText(object.getString("status"));

        super.getItemView(object, v, parent);

        return v;
    }

}
