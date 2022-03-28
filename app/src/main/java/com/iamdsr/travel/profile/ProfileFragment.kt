package com.iamdsr.travel.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.iamdsr.travel.AppLaunchSetup.LoginActivity
import com.iamdsr.travel.R
import com.iamdsr.travel.viewModels.SearchMemberFragmentViewModel
import java.util.HashMap


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
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.layout_dialog_confirm_cancel, null)
        val mDialogTitle  = dialogView.findViewById<View>(R.id.title) as TextView
        val mDialogDesc  = dialogView.findViewById<View>(R.id.desc) as TextView
        val mDialogCancelBtn  = dialogView.findViewById<View>(R.id.cancel) as Button
        val mDialogConfirmBtn  = dialogView.findViewById<View>(R.id.confirm) as Button

        mDialogTitle.setText(R.string.sign_out_title)
        mDialogDesc.setText(R.string.sign_out_message)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        mDialogCancelBtn.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        mDialogConfirmBtn.setOnClickListener(View.OnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null){
                FirebaseAuth.getInstance().signOut()
                dialog.dismiss()
            }
        })
    }

    private fun setupWidgets() {
        if (view != null){
            mSignOutButton = view!!.findViewById(R.id.sign_out)
        }
    }
}
