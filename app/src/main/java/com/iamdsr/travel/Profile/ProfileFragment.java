package com.iamdsr.travel.Profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iamdsr.travel.R;

public class ProfileFragment extends Fragment {
    private View view;
    public ProfileFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        setUpWidgets();
        setUpFirebase();
        return view;
    }

    private void setUpFirebase() {

    }
    private void setUpWidgets() {
    }
}