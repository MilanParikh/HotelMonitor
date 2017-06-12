package com.milanparikh.hotelmonitor.Client.CheckListFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.milanparikh.hotelmonitor.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MaintenanceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MaintenanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MaintenanceFragment extends android.support.v4.app.Fragment {

    private OnFragmentInteractionListener mListener;

    CheckBox maintenanceToilet;
    CheckBox maintenanceShowerDrain;
    CheckBox maintenanceShowerCurtain;
    CheckBox maintenanceAirConditioning;
    CheckBox maintenanceTelevision;
    CheckBox maintenanceRemote;
    CheckBox maintenanceWindowCurtain;
    CheckBox maintenanceCarpet;
    CheckBox maintenanceRoomLock;

    public MaintenanceFragment() {
        // Required empty public constructor
    }

    public static MaintenanceFragment newInstance() {
        MaintenanceFragment fragment = new MaintenanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maintenance, container, false);
        maintenanceToilet = (CheckBox)view.findViewById(R.id.maintenance_toilet);
        maintenanceShowerDrain = (CheckBox)view.findViewById(R.id.maintenance_shower_drain);
        maintenanceShowerCurtain = (CheckBox)view.findViewById(R.id.maintenance_shower_curtain);
        maintenanceAirConditioning = (CheckBox)view.findViewById(R.id.maintenance_air_conditioning);
        maintenanceTelevision = (CheckBox)view.findViewById(R.id.maintenance_television);
        maintenanceRemote = (CheckBox)view.findViewById(R.id.maintenance_remote);
        maintenanceWindowCurtain = (CheckBox)view.findViewById(R.id.maintenance_window_curtain);
        maintenanceCarpet = (CheckBox)view.findViewById(R.id.maintenance_carpet);
        maintenanceRoomLock = (CheckBox)view.findViewById(R.id.maintenance_room_lock);
        return view;
    }

    public FragmentData.MaintenanceValues getMaintenanceValues() {
        FragmentData.MaintenanceValues maintenanceValues = new FragmentData.MaintenanceValues();
        maintenanceValues.setMaintenanceToiletInt(maintenanceToilet.isChecked() ? 1 : 0);
        maintenanceValues.setMaintenanceShowerDrainInt(maintenanceShowerDrain.isChecked() ? 1 : 0);
        maintenanceValues.setMaintenanceShowerCurtainInt(maintenanceShowerCurtain.isChecked() ? 1 : 0);
        maintenanceValues.setMaintenanceAirConditioningInt(maintenanceAirConditioning.isChecked() ? 1 : 0);
        maintenanceValues.setMaintenanceTelevisionInt(maintenanceTelevision.isChecked() ? 1 : 0);
        maintenanceValues.setMaintenanceRemoteInt(maintenanceRemote.isChecked() ? 1 : 0);
        maintenanceValues.setMaintenanceWindowCurtainInt(maintenanceWindowCurtain.isChecked() ? 1 : 0);
        maintenanceValues.setMaintenanceCarpetInt(maintenanceCarpet.isChecked() ? 1 : 0);
        maintenanceValues.setMaintenanceRoomLockInt(maintenanceRoomLock.isChecked() ? 1 : 0);
        return maintenanceValues;
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
