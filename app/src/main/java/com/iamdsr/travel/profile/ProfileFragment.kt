package com.iamdsr.travel.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.utils.AppConstants
import com.iamdsr.travel.utils.MySharedPreferences


class ProfileFragment: Fragment(){

    // Widgets
    private lateinit var mSignOutButton: TextView
    private lateinit var mAppTheme: TextView

    //Utils
    private lateinit var sharedPreferenceHelper: MySharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        sharedPreferenceHelper = MySharedPreferences(context!!)

        mSignOutButton.setOnClickListener(View.OnClickListener {
            setDialogForSignOut()
        })
        mAppTheme.setOnClickListener(View.OnClickListener {
            setUpThemeDialog()
        })
    }

    private fun setUpThemeDialog() {
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.layout_dialog_app_theme, null)
        val mDialogCancelBtn  = dialogView.findViewById<View>(R.id.cancel) as Button
        val mDialogConfirmBtn  = dialogView.findViewById<View>(R.id.confirm) as Button
        val radioGroup = dialogView.findViewById(R.id.theme_radio_group) as RadioGroup

        if (sharedPreferenceHelper.getAppTheme() == AppConstants.DARK_THEME) {
            (dialogView.findViewById(R.id.dark_theme) as RadioButton).isChecked = true
        } else if (sharedPreferenceHelper.getAppTheme() == AppConstants.LIGHT_THEME) {
            (dialogView.findViewById(R.id.light_theme) as RadioButton).isChecked = true
        } else {
            (dialogView.findViewById(R.id.sys_theme) as RadioButton).isChecked = true
        }

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        mDialogCancelBtn.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        mDialogConfirmBtn.setOnClickListener(View.OnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val themeSelected = dialogView.findViewById(selectedId) as RadioButton
            if (themeSelected.text == context!!.resources.getString(R.string.light_theme)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferenceHelper.setAppTheme(AppConstants.LIGHT_THEME);
            }
            else if (themeSelected.text == context!!.resources.getString(R.string.dark_theme)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPreferenceHelper.setAppTheme(AppConstants.DARK_THEME);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                sharedPreferenceHelper.setAppTheme(AppConstants.SYSTEM_DEFAULT_THEME);
            }
            dialog.dismiss()
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
            mAppTheme = view!!.findViewById(R.id.app_theme)
        }
    }
}
