package com.iamdsr.travel.calculateExpenses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.ExpenseGroupRecyclerAdapter
import com.iamdsr.travel.customRecyclerViewAdapters.PlannedTripRecyclerAdapter
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.repositories.FirestoreRepository
import com.iamdsr.travel.viewModels.CalculateExpenseViewModel
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalculateExpenseFragment : Fragment(), RecyclerViewActionsInterface{

    // Widgets
    private lateinit var mCreateNewGroup: Button
    private lateinit var mExpenseGroupRecyclerView: RecyclerView

    // Utils
    private lateinit var expenseGroupList: List<ExpenseGroupModel>
    private lateinit var expenseGroupRecyclerAdapter: ExpenseGroupRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calculate_expense, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        initRecyclerView()
        val calculateExpenseViewModel = ViewModelProvider(requireActivity())[CalculateExpenseViewModel::class.java]
        calculateExpenseViewModel._getAllSavedExpenseGroupsFromFirebaseFirestore(FirebaseAuth.getInstance().currentUser!!.displayName.toString()).observe(this, Observer { it->
            expenseGroupList = it
            Log.d("TAG", "onViewCreated: IT $it")
            expenseGroupRecyclerAdapter.submitList(it)
        })
        mCreateNewGroup.setOnClickListener(View.OnClickListener {
            setupAlertDialog()
        })
    }

    private fun initRecyclerView() {
        mExpenseGroupRecyclerView.layoutManager = LinearLayoutManager(context)
        mExpenseGroupRecyclerView.setHasFixedSize(true)
        expenseGroupRecyclerAdapter = ExpenseGroupRecyclerAdapter(this)
        mExpenseGroupRecyclerView.adapter = expenseGroupRecyclerAdapter
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
                memberList.add(FirebaseAuth.getInstance().currentUser!!.displayName!!)
                Log.d("TAG", "setupAlertDialog: Display name ${FirebaseAuth.getInstance().currentUser!!.displayName!!}")
                val expenseGroupModel = ExpenseGroupModel(
                    groupID,
                    groupName,
                    "",
                    1,
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    memberList,
                    getTimestamp()
                )
                Log.d("TAG", "setupAlertDialog: Model $expenseGroupModel")
                calculateExpenseViewModel._addNewExpenseGroupToFirebaseFirestore(expenseGroupModel)
            }
            .show()
    }

    private fun getTimestamp(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getDefault()
        return format.format(Date())
    }

    private fun setupWidgets() {
        if (view!=null) {
            mCreateNewGroup = view!!.findViewById(R.id.create_new_group)
            mExpenseGroupRecyclerView = view!!.findViewById(R.id.group_recycler_view)
        }
    }

    override fun onItemClick(view: View, position: Int) {

    }
}