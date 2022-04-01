package com.iamdsr.travel.ActivityUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.iamdsr.travel.AppLaunchSetup.LoginActivity;
import com.iamdsr.travel.R;

public class MainActivity extends AppCompatActivity {

    //Firebase variables
    private FirebaseAuth firebaseAuth;

    //widgets
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFirebase();

//        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavController.navigate(R.id.ac);
//            }
//        });
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
    }
}