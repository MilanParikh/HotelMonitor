package com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by milan on 6/28/2017.
 */

public class MasterEmployeeRoomListAdapter<T extends ParseObject> extends ParseQueryAdapter {
    Date outDate;
    Date currentDate;
    ParseObject roomObject;
    String currentName;

    public MasterEmployeeRoomListAdapter(Context context, QueryFactory<T> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_master_roomlist, null);
        }

        super.getItemView(object, v, parent);
        roomObject = object;

        TextView roomNum = (TextView)v.findViewById(R.id.room_num);
        roomNum.setText(Integer.toString(object.getInt("room")));

        currentName = object.getString("current_name");
        TextView currentNameText = (TextView)v.findViewById(R.id.current_name);
        currentNameText.setText(currentName);

        TextView guestStatusText = (TextView)v.findViewById(R.id.guest_status_text);
        String checkin = object.getString("checkindate");
        String checkout = object.getString("checkoutdate");

        if(checkin==null && checkout==null) {
            guestStatusText.setText("Vacant");
        }else if(checkin!=null && checkout==null){
            guestStatusText.setText("Vacant");
        }else if(checkin==null && checkout!=null){
            guestStatusText.setText("Vacant");
        }else if(checkin!=null && checkout!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            try{
                outDate = sdf.parse(checkout);
                currentDate = sdf.parse(sdf.format(new Date()));
            }catch (java.text.ParseException e){
                e.printStackTrace();
            }
            if(currentDate.equals(outDate)){
                guestStatusText.setText(R.string.due_out);
            }else if(currentDate.before(outDate)){
                guestStatusText.setText(R.string.stay_over);
            }else if(currentDate.after(outDate)){
                guestStatusText.setText(R.string.overstayed);
            }

        }

        TextView guestDuration = (TextView)v.findViewById(R.id.guest_duration_text);
        guestDuration.setVisibility(View.GONE);
        TextView membershipText = (TextView)v.findViewById(R.id.membership_master);
        membershipText.setVisibility(View.GONE);

        int clean = object.getInt("clean");
        switch (clean) {
            case 0:
                v.setBackgroundColor(getContext().getColor(R.color.roomListRed));
                break;
            case 1:
                v.setBackgroundColor(getContext().getColor(R.color.roomListYellow));
                break;
            case 2:
                v.setBackgroundColor(getContext().getColor(R.color.roomListGreen));
                break;
            case 3:
                v.setBackgroundColor(getContext().getColor(R.color.roomListBlue));
                break;
        }

        return v;
    }
}
