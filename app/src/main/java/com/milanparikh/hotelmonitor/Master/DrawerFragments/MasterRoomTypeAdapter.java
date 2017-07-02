package com.milanparikh.hotelmonitor.Master.DrawerFragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;


/**
 * Created by milan on 7/2/2017.
 */

public class MasterRoomTypeAdapter<T extends ParseObject> extends ParseQueryAdapter {

    public MasterRoomTypeAdapter(Context context, QueryFactory<T> queryFactory){
        super(context, queryFactory);
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent){
        if (v==null){
            v = View.inflate(getContext(), R.layout.item_master_room_type, null);
        }

        super.getItemView(object, v, parent);

        TextView acronymText = (TextView)v.findViewById(R.id.room_acronym_text);
        TextView descriptionText = (TextView)v.findViewById(R.id.room_description_text);

        acronymText.setText(object.getString("acronym"));
        descriptionText.setText(object.getString("description"));

        return v;
    }

}
