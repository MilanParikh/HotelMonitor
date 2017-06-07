package com.milanparikh.hotelmonitor.Client;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by milan on 6/7/2017.
 */

public class ClientRoomListAdapter extends ParseQueryAdapter {

    public ClientRoomListAdapter(Context context, QueryFactory<ParseObject> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.client_roomlist_item, null);
        }

        super.getItemView(object, v, parent);

        TextView roomNum = (TextView)v.findViewById(R.id.room_num);
        roomNum.setText(object.getString("room"));

        return v;
    }

}
