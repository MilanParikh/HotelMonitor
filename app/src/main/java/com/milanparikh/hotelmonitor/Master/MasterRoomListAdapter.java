package com.milanparikh.hotelmonitor.Master;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.IntegerRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/**
 * Created by milan on 6/6/2017.
 */

public class MasterRoomListAdapter extends ParseQueryAdapter {

    public MasterRoomListAdapter(Context context, QueryFactory<ParseObject> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.master_roomlist_item, null);
        }

        super.getItemView(object, v, parent);

        TextView roomNum = (TextView)v.findViewById(R.id.room_num);
        roomNum.setText(object.getString("room"));

        Boolean clean = object.getBoolean("clean");
        if (clean) {
            v.setBackgroundColor(Color.GREEN);
        }
        else {
            v.setBackgroundColor(Color.RED);
        }

        RadioGroup statusGroup = (RadioGroup)v.findViewById(R.id.room_status_group);
        int status = object.getInt("status");
        if (status==0) {
            statusGroup.check(R.id.due_out_button);
        }
        else if (status==1) {
            statusGroup.check(R.id.stay_over_button);
        }

        statusGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.due_out_button:
                        object.put("status", 0);
                        object.saveInBackground();
                        break;
                    case R.id.stay_over_button:
                        object.put("status",1);
                        object.saveInBackground();
                }
            }
        });

        return v;
    }

}
