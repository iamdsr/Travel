package com.iamdsr.travel.PlanTrip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.iamdsr.travel.CustomRecyclerViewAdapters.PlannedTripsRecyclerAdapter;
import com.iamdsr.travel.Interfaces.RecyclerViewActionsInterface;
import com.iamdsr.travel.Models.TripModel;
import com.iamdsr.travel.R;
import com.iamdsr.travel.ViewModels.PlanTripFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class PlanTripFragment extends Fragment implements RecyclerViewActionsInterface {
    //Widgets
    private View view;
    private ExtendedFloatingActionButton mAddTrip;
    private RecyclerView mPlannedTripRecyclerView;
    private PlannedTripsRecyclerAdapter plannedTripsRecyclerAdapter;

    //Utils
    private static final String TAG = "TAG";
    private PlanTripFragmentViewModel planTripFragmentViewModel;
    private List<TripModel> planList = new ArrayList<TripModel>();

    public PlanTripFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_plan_trip, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpWidgets();
        planTripFragmentViewModel = new ViewModelProvider(getActivity()).get(PlanTripFragmentViewModel.class);
        planTripFragmentViewModel.getPlannedTripListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<TripModel>>() {
            @Override
            public void onChanged(List<TripModel> tripModelList) {
                planList = new ArrayList<>(tripModelList);
                Log.d("TAG", "----------------------------------------- ON CHANGED -------------------------------------------------------");
                Log.d(TAG, "onChanged: "+tripModelList.size());
                for (int i = 0; i < tripModelList.size(); i++) {
                    Log.d("TAG", "onChanged INSIDE FOR: "+i+" : "+tripModelList.get(i));
                }
                plannedTripsRecyclerAdapter.submitList(tripModelList);
            }
        });
        mAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_planTripFragment_to_addTripFragment);
            }
        });
        initRecyclerView();
    }
    private void initRecyclerView(){
        mPlannedTripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPlannedTripRecyclerView.setHasFixedSize(true);
        plannedTripsRecyclerAdapter = new PlannedTripsRecyclerAdapter(this);
        mPlannedTripRecyclerView.setAdapter(plannedTripsRecyclerAdapter);
        //plannedTripsRecyclerAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }
    private void setUpWidgets() {
        mAddTrip = view.findViewById(R.id.add_new_trip);
        mPlannedTripRecyclerView = view.findViewById(R.id.trip_recycler_view);
    }

    @Override
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("TRIP_ID",planList.get(position).getTrip_id());
        bundle.putString("TRIP_TITLE",planList.get(position).getTrip_title());
        bundle.putString("TRIP_DESCRIPTION",planList.get(position).getTrip_desc());
        bundle.putString("JOURNEY_DATE",planList.get(position).getJourney_date());
        bundle.putString("RETURN_DATE",planList.get(position).getReturn_date());
        bundle.putString("PLACE_FROM",planList.get(position).getPlace_from());
        bundle.putString("PLACE_TO",planList.get(position).getPlace_to());
        bundle.putLong("TOTAL_PAX",planList.get(position).getTotal_heads());
        bundle.putString("DATE_PLANNED",planList.get(position).getDate_created());
        bundle.putLong("DURATION",planList.get(position).getDuration_in_days());
        bundle.putString("PLANNED_BY",planList.get(position).getUser_id());
        Navigation.findNavController(view).navigate(R.id.action_planTripFragment_to_updateTripFragment, bundle);
    }
}