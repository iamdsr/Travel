package com.iamdsr.travel.CalculateExpenses;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iamdsr.travel.R;

public class CalculateExpenseFragment extends Fragment {
    private View view;
    public CalculateExpenseFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calculate_expense, container, false);
        setUpWidgets();
        setUpFirebase();
        return view;
    }

    private void setUpFirebase() {

    }
    private void setUpWidgets() {
    }
}