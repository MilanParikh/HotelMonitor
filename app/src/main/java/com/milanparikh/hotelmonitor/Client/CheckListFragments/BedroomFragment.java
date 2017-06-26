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
 * {@link BedroomFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BedroomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BedroomFragment extends android.support.v4.app.Fragment {

    private OnFragmentInteractionListener mListener;

    CheckBox bedroomDresser;
    CheckBox bedroomCoffeeStation;
    CheckBox bedroomCarpetFloor;
    CheckBox bedroomWindowsSills;
    CheckBox bedroomBlanketLinen;
    CheckBox bedroomBedspread;
    CheckBox bedroomUnderBed;
    CheckBox bedroomPhones;
    CheckBox bedroomPicturesMirrors;
    CheckBox bedroomEntranceDoor;
    CheckBox bedroomFridge;
    CheckBox bedroomPtac;
    CheckBox bedroomLampShades;

    public BedroomFragment() {
        // Required empty public constructor
    }

    public static BedroomFragment newInstance() {
        BedroomFragment fragment = new BedroomFragment();
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
        View view = inflater.inflate(R.layout.fragment_bedroom, container, false);
        bedroomDresser = (CheckBox)view.findViewById(R.id.bedroom_dresser);
        bedroomCoffeeStation = (CheckBox)view.findViewById(R.id.bedroom_coffee_station);
        bedroomCarpetFloor = (CheckBox)view.findViewById(R.id.bedroom_carpet_floor);
        bedroomWindowsSills = (CheckBox)view.findViewById(R.id.bedroom_windows_sills);
        bedroomBlanketLinen = (CheckBox)view.findViewById(R.id.bedroom_blanket_linen);
        bedroomBedspread = (CheckBox)view.findViewById(R.id.bedroom_bedspread);
        bedroomUnderBed = (CheckBox)view.findViewById(R.id.bedroom_under_bed);
        bedroomPhones = (CheckBox)view.findViewById(R.id.bedroom_phones);
        bedroomPicturesMirrors = (CheckBox)view.findViewById(R.id.bedroom_pictures_mirrors);
        bedroomEntranceDoor = (CheckBox)view.findViewById(R.id.bedroom_entrance_door);
        bedroomFridge = (CheckBox)view.findViewById(R.id.bedroom_fridge);
        bedroomPtac = (CheckBox)view.findViewById(R.id.bedroom_ptac);
        bedroomLampShades = (CheckBox)view.findViewById(R.id.bedroom_lamp_shades);
        return view;
    }

    public FragmentData.BedroomValues getBedroomValues() {
        FragmentData.BedroomValues bedroomValues = new FragmentData.BedroomValues();
        bedroomValues.setBedroomDresserInt(bedroomDresser.isChecked() ? 1 : 0);
        bedroomValues.setBedroomCoffeeStationInt(bedroomCoffeeStation.isChecked() ? 1 : 0);
        bedroomValues.setBedroomCarpetFloorInt(bedroomCarpetFloor.isChecked() ? 1 : 0);
        bedroomValues.setBedroomWindowsSillsInt(bedroomWindowsSills.isChecked() ? 1 : 0);
        bedroomValues.setBedroomBlanketLinenInt(bedroomBlanketLinen.isChecked() ? 1 : 0);
        bedroomValues.setBedroomBedspreadInt(bedroomBedspread.isChecked() ? 1 : 0);
        bedroomValues.setBedroomUnderBedInt(bedroomUnderBed.isChecked() ? 1 : 0);
        bedroomValues.setBedroomPhonesInt(bedroomPhones.isChecked() ? 1 : 0);
        bedroomValues.setBedroomPicturesMirrorsInt(bedroomPicturesMirrors.isChecked() ? 1 : 0);
        bedroomValues.setBedroomEntranceDoorInt(bedroomEntranceDoor.isChecked() ? 1 : 0);
        bedroomValues.setBedroomFridgeInt(bedroomFridge.isChecked() ? 1 : 0);
        bedroomValues.setBedroomPtacInt(bedroomPtac.isChecked() ? 1 : 0);
        bedroomValues.setBedroomLampShadesInt(bedroomLampShades.isChecked() ? 1 : 0);
        return bedroomValues;
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
