package com.iamdsr.travel.calculateExpenses

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.AddExpenseFragmentRecyclerAdapter
import com.iamdsr.travel.customRecyclerViewAdapters.SearchMemberRecyclerAdapter
import com.iamdsr.travel.interfaces.MyFirestoreInterface
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.viewModels.AddExpenseFragmentViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

class ExpensesFragment : Fragment(), RecyclerViewActionsInterface {

    // Widgets
    private lateinit var mAddMember: Button
    private lateinit var mGroupName: TextView
    private lateinit var mHighlightMessage: TextView
    private lateinit var mAddExpense: ExtendedFloatingActionButton
    private lateinit var mExpensesRecyclerView: RecyclerView

    // Utils
    private var groupID: String = ""
    private var expenseGroupModel = ExpenseGroupModel()
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
        val addExpenseFragmentViewModel = ViewModelProvider(this)[AddExpenseFragmentViewModel::class.java]
        addExpenseFragmentViewModel._getAllSavedExpenseFromFirebaseFirestore(groupID).observe(requireActivity(), Observer {
            Log.d("TAG", "_getAllSavedExpenseFromFirebaseFirestore: $it")
            addExpenseFragmentRecyclerAdapter.submitList(it)
        })
        addExpenseFragmentViewModel._getMembersPayStatusLiveDataFromGroup(expenseGroupModel.id, object : MyFirestoreInterface{
            override fun onExpenseGroupModelUpdateCallback(model: ExpenseGroupModel) {

            }
            override fun onExpenseGroupModelUpdateLiveDataCallback(liveData: MutableLiveData<ExpenseGroupModel?>) {
                liveData.observe(requireActivity(), Observer {
                    val model: ExpenseGroupModel? = it
                    if (model!=null){
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
            }
        })

        mAddExpense.setOnClickListener(View.OnClickListener {
            addExpenseFragmentViewModel._getMembersPayStatusFromGroup(expenseGroupModel.id, object : MyFirestoreInterface{
                override fun onExpenseGroupModelUpdateCallback(model: ExpenseGroupModel) {
                    val bundle = Bundle()
                    bundle.putSerializable("EXPENSE_GROUP_MODEL",  model)
                    findNavController().navigate(R.id.action_expensesFragment_to_addExpenseFragment, bundle)
                }

                override fun onExpenseGroupModelUpdateLiveDataCallback(liveData: MutableLiveData<ExpenseGroupModel?>) {

                }
            })

        })
        mAddMember.setOnClickListener(View.OnClickListener {
            addExpenseFragmentViewModel._getMembersPayStatusFromGroup(expenseGroupModel.id, object : MyFirestoreInterface{
                override fun onExpenseGroupModelUpdateCallback(model: ExpenseGroupModel) {
                    val bundle = Bundle()
                    bundle.putString("EXPENSE_GROUP_ID",  model.id)
                    bundle.putString("GROUP_CREATED_BY",model.created_by_id)
                    findNavController().navigate(R.id.action_expensesFragment_to_searchMemberFragment, bundle)
                }

                override fun onExpenseGroupModelUpdateLiveDataCallback(liveData: MutableLiveData<ExpenseGroupModel?>) {

                }
            })

        })
    }

    private fun initRecyclerView() {
        mExpensesRecyclerView.layoutManager = LinearLayoutManager(context)
        mExpensesRecyclerView.setHasFixedSize(true)
        addExpenseFragmentRecyclerAdapter = AddExpenseFragmentRecyclerAdapter(this)
        mExpensesRecyclerView.adapter = addExpenseFragmentRecyclerAdapter
    }

    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    private fun setupWidgets() {
        if (view != null){
            mAddMember = view!!.findViewById(R.id.add_group_member)
            mAddExpense = view!!.findViewById(R.id.add_expense)
            mGroupName = view!!.findViewById(R.id.group_name)
            mHighlightMessage = view!!.findViewById(R.id.borrowed_lent)
            mExpensesRecyclerView = view!!.findViewById(R.id.expenses_recycler_view)
        }
    }

    override fun onItemClick(view: View, position: Int) {

    }
}