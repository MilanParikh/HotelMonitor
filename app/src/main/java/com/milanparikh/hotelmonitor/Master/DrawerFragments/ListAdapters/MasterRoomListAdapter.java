package com.milanparikh.hotelmonitor.Master.DrawerFragments.ListAdapters;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by milan on 6/6/2017.
 */

public class MasterRoomListAdapter<T extends ParseObject> extends ParseQueryAdapter {
    Date outDate;
    Date currentDate;

    public MasterRoomListAdapter(Context context, QueryFactory<T> queryFactory) {
        super(context, queryFactory);

    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_master_roomlist, null);
        }

        super.getItemView(object, v, parent);

        TextView roomNum = (TextView)v.findViewById(R.id.room_num);
        roomNum.setText(Integer.toString(object.getInt("room")));

        String[] membershiplist = getContext().getResources().getStringArray(R.array.membership_array);
        int membershipPos = object.getInt("membership");
        TextView membershipText = (TextView)v.findViewById(R.id.membership_master);
        membershipText.setText(membershiplist[membershipPos]);

        String currentName = object.getString("current_name");
        TextView currentNameText = (TextView)v.findViewById(R.id.current_name);
        currentNameText.setText(currentName);

        TextView guestStatusText = (TextView)v.findViewById(R.id.guest_status_text);

        TextView guestDuration = (TextView)v.findViewById(R.id.guest_duration_text);
        String checkin = object.getString("checkindate");
        String checkout = object.getString("checkoutdate");

        if(checkin==null && checkout==null) {
            guestDuration.setVisibility(View.GONE);
            guestStatusText.setText("Vacant");
        }else if(checkin!=null && checkout==null){
            guestDuration.setText(checkin + " - ");
            guestDuration.setVisibility(View.VISIBLE);
            guestStatusText.setText("Vacant");
        }else if(checkin==null && checkout!=null){
            guestDuration.setText(" - " + checkout);
            guestDuration.setVisibility(View.VISIBLE);
            guestStatusText.setText("Vacant");
        }else if(checkin!=null && checkout!=null){
            guestDuration.setText(checkin + " - " + checkout);
            guestDuration.setVisibility(View.VISIBLE);

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

        int clean = object.getInt("clean");
        switch (clean) {
            case 0:
                roomNum.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                v.setBackgroundColor(getContext().getColor(R.color.roomListRed));
                break;
            case 1:
                roomNum.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                v.setBackgroundColor(getContext().getColor(R.color.roomListYellow));
                break;
            case 2:
                roomNum.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                v.setBackgroundColor(getContext().getColor(R.color.roomListGreen));
                break;
            case 3:
                roomNum.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                v.setBackgroundColor(getContext().getColor(R.color.roomListBlue));
                break;
            case 4:
                roomNum.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_black_24dp, 0);
                v.setBackgroundColor(getContext().getColor(R.color.roomListGreen));
                break;
            case 5:
                roomNum.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_warning_black_24dp, 0);
                v.setBackgroundColor(getContext().getColor(R.color.roomListYellow));
                break;
            case 6:
                roomNum.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_warning_black_24dp, 0);
                v.setBackgroundColor(getContext().getColor(R.color.roomListRed));
                break;
        }

        return v;
    }




}
