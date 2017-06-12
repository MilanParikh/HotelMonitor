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
 * {@link BathroomFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BathroomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BathroomFragment extends android.support.v4.app.Fragment {

    private OnFragmentInteractionListener mListener;

    CheckBox bathroomLightFixtures;
    CheckBox bathroomVanity;
    CheckBox bathroomHairDryer;
    CheckBox bathroomPhones;
    CheckBox bathroomToilet;
    CheckBox bathroomTubWalls;
    CheckBox bathroomTubBase;
    CheckBox bathroomShowerHead;

    public BathroomFragment() {
        // Required empty public constructor
    }


    public static BathroomFragment newInstance() {
        BathroomFragment fragment = new BathroomFragment();
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
        View view = inflater.inflate(R.layout.fragment_bathroom, container, false);
        bathroomLightFixtures = (CheckBox)view.findViewById(R.id.bathroom_light_fixtures);
        bathroomVanity = (CheckBox)view.findViewById(R.id.bathroom_vanity);
        bathroomHairDryer = (CheckBox)view.findViewById(R.id.bathroom_hair_dryer);
        bathroomPhones = (CheckBox)view.findViewById(R.id.bathroom_phones);
        bathroomToilet = (CheckBox)view.findViewById(R.id.bathroom_toilet);
        bathroomTubWalls = (CheckBox)view.findViewById(R.id.bathroom_tub_walls);
        bathroomTubBase = (CheckBox)view.findViewById(R.id.bathroom_tub_base);
        bathroomShowerHead = (CheckBox)view.findViewById(R.id.bathroom_shower_head);
        return view;
    }

    public FragmentData.BathroomValues getBathroomValues() {
        FragmentData.BathroomValues bathroomValues = new FragmentData.BathroomValues();
        bathroomValues.setBathroomLightFixturesInt(bathroomLightFixtures.isChecked() ? 1 : 0);
        bathroomValues.setBathroomVanityInt(bathroomVanity.isChecked() ? 1 : 0);
        bathroomValues.setBathroomHairDryerInt(bathroomHairDryer.isChecked() ? 1 : 0);
        bathroomValues.setBathroomPhonesInt(bathroomPhones.isChecked() ? 1 : 0);
        bathroomValues.setBathroomToiletInt(bathroomToilet.isChecked() ? 1 : 0);
        bathroomValues.setBathroomTubWallsInt(bathroomTubWalls.isChecked() ? 1 : 0);
        bathroomValues.setBathroomTubBaseInt(bathroomTubBase.isChecked() ? 1 : 0);
        bathroomValues.setBathroomShowerHeadInt(bathroomShowerHead.isChecked() ? 1 : 0);
        return bathroomValues;
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
