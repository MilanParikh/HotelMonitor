package com.milanparikh.hotelmonitor.Client;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListView;
import com.milanparikh.hotelmonitor.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import org.w3c.dom.Text;

/**
 * Created by milan on 7/14/2017.
 */

public class ChecklistAdapter<T extends ParseObject> extends ParseQueryAdapter {
    String source;
    ParseObject roomListObject;
    CheckedTextView textView;
    String header;

    public ChecklistAdapter(Context context, QueryFactory<T> queryFactory, String from, ParseObject roomObject){
        super(context, queryFactory);
        if(from!=null){
            source = from;
        }else{
            source="";
        }
        roomListObject = roomObject;
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
            if(roomListObject.getInt(header)==1){
                textView.setChecked(true);
            }
        }

        return v;
    }
}
