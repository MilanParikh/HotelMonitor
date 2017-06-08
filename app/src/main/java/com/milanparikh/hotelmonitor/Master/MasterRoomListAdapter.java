package com.milanparikh.hotelmonitor.Master;

import android.content.Context;
import android.graphics.Color;
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

/**
 * Created by milan on 6/6/2017.
 */

public class MasterRoomListAdapter<T extends ParseObject> extends ParseQueryAdapter {

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
        }

        TextView guestStatusText = (TextView)v.findViewById(R.id.guest_status_text);
        int guestStatus = object.getInt("status");
        switch (guestStatus) {
            case 0:
                guestStatusText.setText("Due Out");
                break;
            case 1:
                guestStatusText.setText("Stay Over");
                break;
            default:
                guestStatusText.setText("Status Unknown");
                break;
        }


        /*RadioGroup statusGroup = (RadioGroup)v.findViewById(R.id.room_status_group);

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
                        break;
                    case R.id.stay_over_button:
                        object.put("status",1);
                        break;
                }
                object.saveInBackground();
            }
        });*/

        return v;
    }




}
