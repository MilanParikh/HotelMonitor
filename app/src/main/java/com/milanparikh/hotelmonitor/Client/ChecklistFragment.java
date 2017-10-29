package com.milanparikh.hotelmonitor.Client;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.milanparikh.hotelmonitor.R;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChecklistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChecklistFragment extends android.support.v4.app.Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private OnFragmentInteractionListener mListener;

    ListView listView;
    ChecklistAdapter<ParseObject> checklistAdapter;
    ParseQuery<ParseObject> query;
    String tabName;
    int checked;
    String source;
    ParseObject roomDataObject;
    ParseObject roomListObject;
    ParseObject maintenanceListObject;
    int total;
    int count=0;

    public ChecklistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    public static ChecklistFragment newInstance(ParseObject roomDataObject, ParseObject roomListObject, ParseObject maintenanceListObject, String tabName, String source){
        ChecklistFragment checklistFragment = new ChecklistFragment();
        Bundle args = new Bundle();
        args.putParcelable("roomListObject", roomListObject);
        args.putParcelable("roomDataObject", roomDataObject);
        args.putParcelable("maintenanceListObject", maintenanceListObject);
        args.putString("tabName", tabName);
        args.putString("source", source);
        checklistFragment.setArguments(args);
        return checklistFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checklist, container, false);
        roomListObject = getArguments().getParcelable("roomListObject");
        roomDataObject = getArguments().getParcelable("roomDataObject");
        maintenanceListObject = getArguments().getParcelable("maintenanceListObject");
        tabName = getArguments().getString("tabName");
        source = getArguments().getString("source");

        if(source.equals("client") && tabName.equals("Maintenance")){
            ParseQuery<ParseObject> maintenanceQuery = ParseQuery.getQuery("Maintenance");
            maintenanceQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e==null){
                        total = objects.size();
                        int i=0;
                        while(i<objects.size()){
                            String header = objects.get(i).getString("header");
                            maintenanceListObject.put(header, 0);
                            i++;
                        }
                        maintenanceListObject.saveInBackground();
                    }
                }
            });
            checklistAdapter = new ChecklistAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
                @Override
                public ParseQuery<ParseObject> create() {
                    query = new ParseQuery<>(tabName);
                    query.orderByAscending("createdAt");
                    return query;
                }
            }, null, null);
        }else if(source.equals("maintenance") || source.equals("master")){
            checklistAdapter = new ChecklistAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
                @Override
                public ParseQuery<ParseObject> create() {
                    query = new ParseQuery<>(tabName);
                    query.orderByAscending("createdAt");
                    return query;
                }
            }, source, maintenanceListObject);
            ParseQuery<ParseObject> maintenanceQuery = ParseQuery.getQuery("Maintenance");
            maintenanceQuery.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if(e==null){
                        total=count;
                    }
                }
            });
        }else{
            checklistAdapter = new ChecklistAdapter<>(getContext(), new ParseQueryAdapter.QueryFactory<ParseObject>() {
                @Override
                public ParseQuery<ParseObject> create() {
                    query = new ParseQuery<>(tabName);
                    query.orderByAscending("createdAt");
                    return query;
                }
            }, null, null);
            ParseQuery<ParseObject> maintenanceQuery = ParseQuery.getQuery("Maintenance");
            maintenanceQuery.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if(e==null){
                        total=count;
                    }
                }
            });
        }
        listView = (ListView)view.findViewById(R.id.checklist_listview);
        listView.setAdapter(checklistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ParseObject checklistItem = (ParseObject)parent.getItemAtPosition(position);
                final String header = checklistItem.getString("header");
                if(listView.isItemChecked(position)){
                    checked=1;
                    count++;
                }else {
                    checked=0;
                    count--;
                }
                switch (source){
                    case "client":
                        roomDataObject.put(header, checked);
                        roomDataObject.saveInBackground();
                        if(tabName.equals("Maintenance")){
                            maintenanceListObject.put(header, checked);
                            maintenanceListObject.saveInBackground();
                        }
                        break;
                    case "maintenance":
                        maintenanceListObject.put(header, checked);
                        maintenanceListObject.saveInBackground();
                        break;
                    case "master":
                        if(tabName.equals("Maintenance")){
                            maintenanceListObject.put(header, checked);
                            maintenanceListObject.saveInBackground();
                        }
                        break;
                }
            }
        });

        if(source.equals("maintenance") || source.equals("master") && tabName.equals("Maintenance")){
            ParseQuery<ParseObject> maintenanceQuery = ParseQuery.getQuery("Maintenance");
            maintenanceQuery.orderByAscending("createdAt");
            maintenanceQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(final List<ParseObject> objects, ParseException e) {
                    maintenanceListObject.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            int i=0;
                            while(i<objects.size()){
                                if(maintenanceListObject.getInt(objects.get(i).getString("header"))==1){
                                    listView.setItemChecked(i, true);
                                    count++;
                                }
                                i++;
                            }
                            i=count;
                        }
                    });

                }
            });
        }


        return view;
    }

    public void setAllChecked(){
        final ParseQuery<ParseObject> checklistQuery = ParseQuery.getQuery(tabName);
        checklistQuery.orderByAscending("createdAt");
        checklistQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                roomDataObject.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (source.equals("client")){
                            int i=0;
                            while(i<objects.size()){
                                roomDataObject.put(objects.get(i).getString("header"), 1);
                                listView.setItemChecked(i, true);
                                i++;
                            }
                            roomDataObject.saveInBackground();
                        }
                        else {
                            int i=0;
                            while(i<objects.size()){
                                listView.setItemChecked(i, true);
                                i++;
                            }
                        }
                    }
                });
                if(tabName.equals("Maintenance")){
                    Log.d("MAINTENANCE", "IT WORKED");
                    maintenanceListObject.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            int i=0;
                            while(i<objects.size()){
                                Log.d("HEADER", objects.get(i).getString("header"));
                                maintenanceListObject.put(objects.get(i).getString("header"), 1);
                                i++;
                            }
                            maintenanceListObject.saveInBackground();
                            count=total;
                        }
                    });
                }
            }
        });
    }

    public int getClean(){
        if (source.equals("master")){
            if(count==total){
                return 4;
            }else{
                return 5;
            }
        }else{
            if(count==total){
                return 2;
            }else{
                return 5;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
