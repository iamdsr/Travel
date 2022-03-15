package com.iamdsr.travel.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.iamdsr.travel.Interfaces.FirestoreTaskCompleteInterface;
import com.iamdsr.travel.Models.TripModel;
import com.iamdsr.travel.Repos.FirebaseRepositories;

import java.util.List;

public class PlanTripFragmentViewModel extends ViewModel implements FirestoreTaskCompleteInterface {

    private MutableLiveData<List<TripModel>> plannedTripListMutableLiveData;
    private FirebaseRepositories firebaseRepositories = new FirebaseRepositories(this);

    public PlanTripFragmentViewModel(){
        if (plannedTripListMutableLiveData == null) {
            plannedTripListMutableLiveData = new MutableLiveData<>();
            firebaseRepositories.getPlannedTripsFromDB();
        }
    }
    public LiveData<List<TripModel>> getPlannedTripListMutableLiveData() {
        //Log.d("TAG", "getPlannedTripListMutableLiveData: "+plannedTripListMutableLiveData);
        return plannedTripListMutableLiveData;
    }

    public void updateTrips(List<TripModel> list){
        firebaseRepositories.getUpdatedPlannedTripsFromDB(list);
    }
    @Override
    public void onNewPlansAdded(List<TripModel> tripModelList) {
        //Log.d("TAG", "onNewPlansAdded: "+tripModelList.size());
        plannedTripListMutableLiveData.setValue(tripModelList);
        //Log.d("TAG", "onNewPlansAdded: "+plannedTripListMutableLiveData);
    }

    @Override
    public void onPlanUpdated(List<TripModel> tripModelList) {
        plannedTripListMutableLiveData.setValue(tripModelList);
    }

    @Override
    public void onException(Exception exception) {

    }
}
