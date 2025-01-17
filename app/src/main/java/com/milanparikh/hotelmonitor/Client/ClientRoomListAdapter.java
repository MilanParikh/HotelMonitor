package com.milanparikh.hotelmonitor.Client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
            v = View.inflate(getContext(), R.layout.item_client_roomlist, null);
        }

        super.getItemView(object, v, parent);

        final ParseUser user = ParseUser.getCurrentUser();

        TextView roomNum = (TextView)v.findViewById(R.id.room_num);
        roomNum.setText(Integer.toString(object.getInt("room")));

        String[] membershiplist = getContext().getResources().getStringArray(R.array.membership_array);
        int membershipPos = object.getInt("membership");
        TextView membershipText = (TextView)v.findViewById(R.id.membership_client);
        membershipText.setText(membershiplist[membershipPos]);

        TextView roomStatus = (TextView)v.findViewById(R.id.room_status);

        String checkin = object.getString("checkindate");
        String checkout = object.getString("checkoutdate");

        if(checkin==null || checkout==null) {
            roomStatus.setText("Vacant");
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
                object.saveInBackground();

                ParseQuery<ParseObject> maintenanceObjectQuery = ParseQuery.getQuery("MaintenanceList");
                maintenanceObjectQuery.whereEqualTo("room", object.getInt("room"));
                maintenanceObjectQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        ParseObject maintenanceListObject = objects.get(0);
                        Intent checklistIntent = new Intent(getContext(),ClientCheckList.class);
                        Bundle extras = new Bundle();
                        extras.putString("objectID",object.getObjectId());
                        extras.putString("source", "client");
                        extras.putParcelable("roomListObject", object);
                        extras.putParcelable("maintenanceListObject", maintenanceListObject);
                        checklistIntent.putExtras(extras);
                        getContext().startActivity(checklistIntent);
                    }
                });
            }
        });

        return v;
    }

}
