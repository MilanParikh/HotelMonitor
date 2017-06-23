package com.milanparikh.hotelmonitor.Master;

import android.content.Context;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.IntegerRes;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

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
            v = View.inflate(getContext(), R.layout.master_roomlist_item, null);
        }

        super.getItemView(object, v, parent);

        TextView roomNum = (TextView)v.findViewById(R.id.room_num);
        roomNum.setText(object.getString("room"));

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
