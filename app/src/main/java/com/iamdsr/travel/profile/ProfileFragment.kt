package com.iamdsr.travel.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.AppLaunchSetup.LoginActivity
import com.iamdsr.travel.R


class ProfileFragment: Fragment(){

    private lateinit var mSignOutButton: TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        mSignOutButton.setOnClickListener(View.OnClickListener {
            setDialogForSignOut()
        })
    }

    private fun setDialogForSignOut() {
        MaterialAlertDialogBuilder(context!!)
            .setTitle(resources.getString(R.string.signout_message))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.confirm)) { dialog, which ->
                if (FirebaseAuth.getInstance().currentUser != null){
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
            .show()
    }

    private fun setupWidgets() {
        if (view != null){
            mSignOutButton = view!!.findViewById(R.id.sign_out)
        }
    }
}
