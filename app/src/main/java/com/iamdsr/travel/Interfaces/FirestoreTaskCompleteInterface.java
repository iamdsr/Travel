package com.iamdsr.travel.Interfaces;

import com.iamdsr.travel.Models.TripModel;

import java.util.List;

public interface FirestoreTaskCompleteInterface {
    void onNewPlansAdded(List<TripModel> tripModelList);
    void onPlanUpdated(List<TripModel> tripModelList);
    void onException(Exception exception);
}
