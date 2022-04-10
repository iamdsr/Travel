package com.iamdsr.travel.activityUtils;


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.AppLaunchSetup.LoginActivity
import com.iamdsr.travel.R


class MainActivity: AppCompatActivity() {

    //Firebase variables
    private lateinit var firebaseAuth:  FirebaseAuth

    //widgets
    private lateinit var bottomNavigationView: BottomNavigationView ;

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupFirebase()
        setupBottomNavigationView()
    }


    private fun setupBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        val navController = navHostFragment?.navController
        setupWithNavController(bottomNavigationView, navHostFragment!!.navController)
        navController!!.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.addNewPostFragment) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
    }
}