package com.iamdsr.travel.ActivityUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.iamdsr.travel.AppLaunchSetup.LoginActivity;
import com.iamdsr.travel.AppLaunchSetup.SignUpActivity;
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
        final boolean[] allFabVisible = {false};
        ExtendedFloatingActionButton floatingActionButton1 = findViewById(R.id.add_new_trip);
        ExtendedFloatingActionButton floatingActionButton2 = findViewById(R.id.add_post);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton2.hide();
        floatingActionButton1.hide();

//        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavController.navigate(R.id.ac);
//            }
//        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allFabVisible[0]){
                    floatingActionButton2.show();
                    floatingActionButton1.show();
                    allFabVisible[0] = true;
                }
                else {
                    floatingActionButton2.hide();
                    floatingActionButton1.hide();
                    allFabVisible[0] = false;
                }
            }
        });
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
    }
}