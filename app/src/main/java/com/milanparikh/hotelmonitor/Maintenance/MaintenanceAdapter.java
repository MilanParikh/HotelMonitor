package com.milanparikh.hotelmonitor.Maintenance;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by milan on 7/11/2017.
 */

public class MaintenanceAdapter<T extends ParseObject> extends ParseQueryAdapter {

    public MaintenanceAdapter(Context context, QueryFactory<T> queryFactory){
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_master_availability, null);
        }
        super.getItemView(object, v, parent);



        return v;
    }

}
