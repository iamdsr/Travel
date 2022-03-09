package com.iamdsr.travel.PlanTrip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.iamdsr.travel.R;


public class PlanTripFragment extends Fragment {
    //Widgets
    private View view;
    private ExtendedFloatingActionButton mAddTrip;
    public PlanTripFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_plan_trip, container, false);
        setUpWidgets();
        setUpFirebase();
        mAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_planTripFragment_to_addTripFragment);
            }
        });
        return view;
    }

    private void setUpFirebase() {

    }
    private void setUpWidgets() {
        mAddTrip = view.findViewById(R.id.add_new_trip);
    }
}