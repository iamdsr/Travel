package com.iamdsr.travel.calculateExpenses

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.iamdsr.travel.R
import com.iamdsr.travel.models.ExpenseGroupModel

class ExpensesFragment : Fragment() {

    // Widgets
    private lateinit var mAddMember: TextView
    private lateinit var mAddExpense: ExtendedFloatingActionButton

    // Utils
    private var groupID: String = ""
    private var expenseGroupModel = ExpenseGroupModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        if (arguments!=null){
            expenseGroupModel = arguments!!.getSerializable("EXPENSE_GROUP_MODEL") as ExpenseGroupModel
            groupID = expenseGroupModel.id
        }
        mAddExpense.setOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("EXPENSE_GROUP_MODEL",  expenseGroupModel)
            findNavController().navigate(R.id.action_expensesFragment_to_addExpenseFragment, bundle)
        })
        mAddMember.setOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putString("EXPENSE_GROUP_ID",  groupID)
            findNavController().navigate(R.id.action_expensesFragment_to_searchMemberFragment, bundle)
        })
    }

    private fun setupWidgets() {
        if (view != null){
            mAddMember = view!!.findViewById(R.id.add_group_member)
            mAddExpense = view!!.findViewById(R.id.add_expense)
        }
    }
}