package com.iamdsr.travel.calculateExpenses

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.AddExpenseFragmentRecyclerAdapter
import com.iamdsr.travel.interfaces.ExpenseManagementFirestoreInterface
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.ExpenseModel
import com.iamdsr.travel.utils.SwipeToDeleteCallback
import com.iamdsr.travel.viewModels.ExpenseManagementViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

class ExpensesFragment : Fragment(), RecyclerViewActionsInterface {

    // Widgets
    private lateinit var mAddMember: Button
    private lateinit var mUpdateExpenseGroup: Button
    private lateinit var mGroupName: TextView
    private lateinit var mHighlightMessage: TextView
    private lateinit var mExpenseInfo: TextView
    private lateinit var mAddExpense: ExtendedFloatingActionButton
    private lateinit var mExpensesRecyclerView: RecyclerView

    // Utils
    private var groupID: String = ""
    private var expenseGroupModel = ExpenseGroupModel()
    private var expenseList: List<ExpenseModel> = arrayListOf()
    private lateinit var addExpenseFragmentRecyclerAdapter: AddExpenseFragmentRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        initRecyclerView()
        if (arguments!=null){
            expenseGroupModel = arguments!!.getSerializable("EXPENSE_GROUP_MODEL") as ExpenseGroupModel
            groupID = expenseGroupModel.id
        }
        val expenseManagementViewModel = ViewModelProvider(this)[ExpenseManagementViewModel::class.java]
        expenseManagementViewModel.getAllSavedExpenses(groupID).observe(requireActivity(), Observer {
            Log.d("TAG", "_getAllSavedExpenseFromFirebaseFirestore: $it")
            expenseList = it
            addExpenseFragmentRecyclerAdapter.submitList(it)
        })
        expenseManagementViewModel.getMemberPaymentsStatusInGroupsRT(expenseGroupModel.id, object : ExpenseManagementFirestoreInterface{
            override fun onExpenseGroupModelUpdateCallback(model: ExpenseGroupModel) {
                mGroupName.text = model.name
                var msg = ""
                for ((memberID, amt) in model.members_payment_status){
                    if (memberID.split("-")[0] == FirebaseAuth.getInstance().currentUser!!.uid) {
                        msg += if (memberID.split("-")[1] == "Borrowed"){
                            "You borrowed ₹${roundOffDecimal(amt)} in total\n"
                        } else{
                            "You lent ₹${roundOffDecimal(amt)} in total\n"
                        }
                    }
                }
                mHighlightMessage.text = msg
            }
        })

        mAddExpense.setOnClickListener(View.OnClickListener {
            expenseManagementViewModel.getMemberPaymentsStatusInGroupsOnce(expenseGroupModel.id, object : ExpenseManagementFirestoreInterface{
                override fun onExpenseGroupModelUpdateCallback(model: ExpenseGroupModel) {
                    val bundle = Bundle()
                    bundle.putSerializable("EXPENSE_GROUP_MODEL",  model)
                    findNavController().navigate(R.id.action_expensesFragment_to_addExpenseFragment, bundle)
                }
            })

        })
        mAddMember.setOnClickListener(View.OnClickListener {
            expenseManagementViewModel.getMemberPaymentsStatusInGroupsOnce(expenseGroupModel.id, object : ExpenseManagementFirestoreInterface{
                override fun onExpenseGroupModelUpdateCallback(model: ExpenseGroupModel) {
                    val bundle = Bundle()
                    bundle.putString("EXPENSE_GROUP_ID",  model.id)
                    bundle.putString("GROUP_CREATED_BY",model.created_by_id)
                    findNavController().navigate(R.id.action_expensesFragment_to_searchMemberFragment, bundle)
                }
            })
        })
        mUpdateExpenseGroup.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_expensesFragment_to_expenseGroupSettingsFragment)
        })
    }

    private fun initRecyclerView() {
        mExpensesRecyclerView.layoutManager = LinearLayoutManager(context)
        mExpensesRecyclerView.setHasFixedSize(true)
        addExpenseFragmentRecyclerAdapter = AddExpenseFragmentRecyclerAdapter(this)
        mExpensesRecyclerView.adapter = addExpenseFragmentRecyclerAdapter
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val expenseItem = expenseList[viewHolder.absoluteAdapterPosition]
                val expenseManagementViewModel = ViewModelProvider(requireActivity())[ExpenseManagementViewModel::class.java]
                expenseManagementViewModel.deleteExpense(expenseItem)
                /*expenseManagementViewModel.getMemberPaymentsStatusInGroupsRT(expenseItem.group_id, object : ExpenseManagementFirestoreInterface{
                    override fun onExpenseGroupModelUpdateCallback(model: ExpenseGroupModel) {
                        updateExpenseGroupOnDelete(expenseItem, model)
                    }
                })*/
                Toast.makeText(context, expenseItem.name+" deleted successfully.", Toast.LENGTH_SHORT).show()

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(mExpensesRecyclerView)
    }

    /*private fun updateExpenseGroupOnDelete(expenseModel: ExpenseModel, expenseGroupModel: ExpenseGroupModel) {

        val paidByID = expenseModel.paid_by_ID
        val memberPaymentStatusMap : MutableMap<String, MutableMap<String, Double>> = mutableMapOf()
        val memberPaymentStatusMapVal : MutableMap<String, Double> = mutableMapOf()
        for ((memberID, amount) in expenseModel.expense_calculation_map){
            for ((memberIDEG, amountEG) in expenseGroupModel.members_payment_status){
                if (memberID == memberIDEG){
                    memberPaymentStatusMapVal[memberIDEG] = amountEG - amount
                }
            }
        }
        memberPaymentStatusMap["members_payment_status"] = memberPaymentStatusMapVal

        val memberExpenseStatusMap : MutableMap<String, MutableMap<String, Double>> = mutableMapOf()
        val memberExpenseStatusMapVal : MutableMap<String, Double> = mutableMapOf()
        for ((memberIDEG, amountEG) in expenseGroupModel.members_expense_status){
            if (memberIDEG == paidByID){
                memberExpenseStatusMapVal[memberIDEG] = amountEG - expenseModel.amount_paid
            }
        }
        memberExpenseStatusMap["members_expense_status"] = memberExpenseStatusMapVal

        Log.d("TAG", "updateExpenseGroupOnDelete: memberPaymentStatusMap : $memberPaymentStatusMap")
        Log.d("TAG", "updateExpenseGroupOnDelete: memberExpenseStatusMap : $memberExpenseStatusMap")

        val expenseManagementViewModel = ViewModelProvider(requireActivity())[ExpenseManagementViewModel::class.java]
        expenseManagementViewModel.updateTotalIndividualMemberExpenses(expenseGroupModel, memberExpenseStatusMap)
        expenseManagementViewModel.updateMemberPaymentsStatusInGroups(expenseGroupModel, memberPaymentStatusMap)

    }*/

    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    private fun setupWidgets() {
        if (view != null){
            mAddMember = view!!.findViewById(R.id.add_group_member)
            mUpdateExpenseGroup = view!!.findViewById(R.id.update_group_details)
            mAddExpense = view!!.findViewById(R.id.add_expense)
            mGroupName = view!!.findViewById(R.id.group_name)
            mHighlightMessage = view!!.findViewById(R.id.borrowed_lent)
            mExpenseInfo = view!!.findViewById(R.id.group_member_list)
            mExpensesRecyclerView = view!!.findViewById(R.id.expenses_recycler_view)
        }
    }

    override fun onItemClick(view: View, position: Int) {

    }
}