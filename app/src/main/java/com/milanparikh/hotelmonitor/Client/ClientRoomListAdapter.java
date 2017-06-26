package com.milanparikh.hotelmonitor.Client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by milan on 6/7/2017.
 */

public class ClientRoomListAdapter extends ParseQueryAdapter {
    Date outDate;
    Date currentDate;

    public ClientRoomListAdapter(Context context, QueryFactory<ParseObject> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.client_roomlist_item, null);
        }

        super.getItemView(object, v, parent);

        final ParseUser user = ParseUser.getCurrentUser();

        TextView roomNum = (TextView)v.findViewById(R.id.room_num);
        roomNum.setText(object.getString("room"));

        String[] membershiplist = getContext().getResources().getStringArray(R.array.membership_array);
        int membershipPos = object.getInt("membership");
        TextView membershipText = (TextView)v.findViewById(R.id.membership_client);
        membershipText.setText(membershiplist[membershipPos]);

        TextView roomStatus = (TextView)v.findViewById(R.id.room_status);

        String checkin = object.getString("checkindate");
        String checkout = object.getString("checkoutdate");

        if(checkin==null || checkout==null) {
            roomStatus.setText(R.string.due_out);
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            try{
                outDate = sdf.parse(checkout);
                currentDate = sdf.parse(sdf.format(new Date()));
            }catch (java.text.ParseException e){
                e.printStackTrace();
            }
            if(currentDate.equals(outDate)){
                roomStatus.setText(R.string.due_out);
            }else if(currentDate.before(outDate)){
                roomStatus.setText(R.string.stay_over);
            }else if(currentDate.after(outDate)){
                roomStatus.setText(R.string.overstayed);
            }

        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.put("clean",1);
                object.put("current_name", user.getUsername());
                object.saveInBackground();
                Intent checklistIntent = new Intent(getContext(),ClientCheckList.class);
                Bundle extras = new Bundle();
                extras.putString("objectID",object.getObjectId());
                checklistIntent.putExtras(extras);
                getContext().startActivity(checklistIntent);
            }
        });

        return v;
    }

}
