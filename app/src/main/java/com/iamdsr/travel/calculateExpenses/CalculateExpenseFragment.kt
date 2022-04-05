package com.iamdsr.travel.calculateExpenses

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.ExpenseGroupRecyclerAdapter
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.repositories.CalculateExpenseFirebaseRepository
import com.iamdsr.travel.viewModels.ExpenseManagementViewModel
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
        val expenseManagementViewModel = ViewModelProvider(requireActivity())[ExpenseManagementViewModel::class.java]
        expenseManagementViewModel.getAllSavedExpenseGroups(FirebaseAuth.getInstance().currentUser!!.displayName.toString()).observe(this, Observer { it->
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
                val expenseManagementViewModel = ViewModelProvider(this)[ExpenseManagementViewModel::class.java]
                val firebaseRepository = CalculateExpenseFirebaseRepository()
                val mGroupName = dialogView.findViewById<View>(R.id.group_name) as TextInputEditText
                val groupName: String = mGroupName.text.toString().trim()
                if (!TextUtils.isEmpty(groupName)){
                    val groupID: String = firebaseRepository.getNewExpenseGroupID()
                    val memberList =ArrayList<String>()
                    memberList.add(FirebaseAuth.getInstance().currentUser!!.displayName!!)

                    val memberIDNameMap : MutableMap<String, Any> = mutableMapOf()
                    memberIDNameMap[FirebaseAuth.getInstance().currentUser!!.uid] = FirebaseAuth.getInstance().currentUser!!.displayName!!

                    val memberPayStatusMap : MutableMap<String, Double> = mutableMapOf()
                    memberPayStatusMap[FirebaseAuth.getInstance().currentUser!!.uid+"-Borrowed"] = 0.0
                    memberPayStatusMap[FirebaseAuth.getInstance().currentUser!!.uid+"-Lent"] = 0.0

                    val memberExpensesMap : MutableMap<String, Double> = mutableMapOf()
                    memberExpensesMap[FirebaseAuth.getInstance().currentUser!!.uid] = 0.0

                    Log.d("TAG", "setupAlertDialog: Display name ${FirebaseAuth.getInstance().currentUser!!.displayName!!}")
                    val expenseGroupModel = ExpenseGroupModel(
                        groupID,
                        groupName,
                        "",
                        1,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        memberList,
                        memberIDNameMap,
                        memberPayStatusMap,
                        memberExpensesMap,
                        getTimestamp()
                    )
                    Log.d("TAG", "setupAlertDialog: Model $expenseGroupModel")
                    expenseManagementViewModel.addNewExpenseGroup(expenseGroupModel)
                }
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
        val bundle = Bundle()
        bundle.putSerializable("EXPENSE_GROUP_MODEL", expenseGroupList[position])
        findNavController().navigate(R.id.action_calculateExpenseFragment_to_expensesFragment, bundle)
    }
}