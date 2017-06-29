package com.milanparikh.hotelmonitor.Master.DrawerFragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by milan on 6/28/2017.
 */

public class MasterEmployeeListAdapter<T extends ParseUser> extends ParseQueryAdapter {

    public MasterEmployeeListAdapter(Context context, QueryFactory<T> queryFactory){
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject user, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.employee_list_item, null);
        }

        TextView employeeName = (TextView)v.findViewById(R.id.employee_name);
        employeeName.setText(user.getString("username"));

        super.getItemView(user, v, parent);

        return v;
    }


}
