package com.iamdsr.travel.calculateExpenses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.iamdsr.travel.R

class ExpensesFragment : Fragment() {

    private var groupID: String = ""
    private lateinit var mAddMember: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        if (arguments!=null){
            groupID = arguments!!.getString("EXPENSE_GROUP_ID","")
        }
        mAddMember.setOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putString("EXPENSE_GROUP_ID",  groupID)
            findNavController().navigate(R.id.action_expensesFragment_to_searchMemberFragment, bundle)
        })
    }

    private fun setupWidgets() {
        if (view != null){
            mAddMember = view!!.findViewById(R.id.add_group_member)
        }
    }
}