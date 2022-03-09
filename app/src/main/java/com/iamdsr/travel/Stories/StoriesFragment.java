package com.iamdsr.travel.Stories;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iamdsr.travel.R;

public class StoriesFragment extends Fragment {
    private View view;
    public StoriesFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_stories, container, false);
        setUpWidgets();
        setUpFirebase();
        return view;
    }

    private void setUpFirebase() {

    }
    private void setUpWidgets() {
    }
}