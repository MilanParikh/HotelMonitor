package com.milanparikh.hotelmonitor.Client;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by milan on 7/14/2017.
 */

public class ChecklistAdapter<T extends ParseObject> extends ParseQueryAdapter {
    String source;
    ParseObject maintenanceListObject;
    CheckedTextView textView;
    String header;

    public ChecklistAdapter(Context context, QueryFactory<T> queryFactory, String from, ParseObject maintenanceObject){
        super(context, queryFactory);
        if(from!=null){
            source = from;
        }else{
            source="";
        }
        maintenanceListObject = maintenanceObject;
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_checklist, null);
        }

        super.getItemView(object, v, parent);

        textView = (CheckedTextView)v.findViewById(R.id.checklist_item_text);
        textView.setText(object.getString("text"));

        header = object.getString("header");
        if(source.equals("maintenance")){
            if(maintenanceListObject.getInt(header)==1){
                textView.setChecked(true);
            }
        }

        return v;
    }
}
