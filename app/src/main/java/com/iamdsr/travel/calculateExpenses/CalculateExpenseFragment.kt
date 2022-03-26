package com.iamdsr.travel.calculateExpenses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.repositories.FirestoreRepository
import com.iamdsr.travel.viewModels.CalculateExpenseViewModel
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel


class CalculateExpenseFragment : Fragment(){

    // Widgets
    private lateinit var mCreateNewGroup: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calculate_expense, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        mCreateNewGroup.setOnClickListener(View.OnClickListener {
            setupAlertDialog()
        })
    }

    private fun setupAlertDialog() {

        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.layout_dialog_create_group, null)
        MaterialAlertDialogBuilder(context!!)
            .setTitle(resources.getString(R.string.create_new_group))
            .setView(dialogView)
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.create)) { dialog, which ->
                val calculateExpenseViewModel = ViewModelProvider(this)[CalculateExpenseViewModel::class.java]
                val firebaseRepository = FirestoreRepository()
                val mGroupName = dialogView.findViewById<View>(R.id.group_name) as TextInputEditText
                val groupName: String = mGroupName.text.toString().trim()
                val groupID: String = firebaseRepository.getNewExpenseGroupID()
                val memberList =ArrayList<String>()
                memberList.add(FirebaseAuth.getInstance().currentUser!!.displayName!!+";"+FirebaseAuth.getInstance().currentUser!!.uid)
                Log.d("TAG", "setupAlertDialog: Display name ${FirebaseAuth.getInstance().currentUser!!.displayName!!}")
                val expenseGroupModel = ExpenseGroupModel(
                    groupID,
                    groupName,
                    "",
                    1,
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    memberList
                )
                Log.d("TAG", "setupAlertDialog: Model $expenseGroupModel")
                calculateExpenseViewModel._addNewExpenseGroupToFirebaseFirestore(expenseGroupModel)
            }
            .show()

//            val editText = dialogView.findViewById<View>(R.id.label_field) as EditText
//            editText.setText("test label")
//        val alertDialog: AlertDialog = dialogBuilder.create()
//        alertDialog.show()
    }

    private fun setupWidgets() {
        if (view!=null) {
            mCreateNewGroup = view!!.findViewById(R.id.create_new_group)
        }
    }
}