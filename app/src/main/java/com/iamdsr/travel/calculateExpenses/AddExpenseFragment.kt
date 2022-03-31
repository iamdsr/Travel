package com.iamdsr.travel.calculateExpenses

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.MultiAutoCompleteTextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.iamdsr.travel.R
import com.iamdsr.travel.interfaces.MyFirestoreInterface
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.ExpenseModel
import com.iamdsr.travel.repositories.CalculateExpenseFirebaseRepository
import com.iamdsr.travel.viewModels.AddExpenseFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*


class AddExpenseFragment : Fragment() {

    // widgets
    private lateinit var mSplitMode: AutoCompleteTextView
    private lateinit var mPaidBy: AutoCompleteTextView
    private lateinit var mDivideAmong: MultiAutoCompleteTextView
    private lateinit var mExpenseDescription: TextInputEditText
    private lateinit var mExpenseAmount: TextInputEditText
    private lateinit var mAddExpenseBtn: Button

    // utils
    private var expenseGroupModel = ExpenseGroupModel()
    private var splitModeEntries = arrayOf("Equally")
    private var paidByEntries = arrayOf("")
    private var divideAmongEntries = arrayOf("")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null){
            expenseGroupModel = arguments!!.getSerializable("EXPENSE_GROUP_MODEL") as ExpenseGroupModel
            paidByEntries = expenseGroupModel.members_name.toTypedArray()
            divideAmongEntries = expenseGroupModel.members_name.toTypedArray()
        }
        setupWidgets()

        mAddExpenseBtn.setOnClickListener(View.OnClickListener {
            addExpenseToDatabase()
        })
    }

    private fun addExpenseToDatabase() {

        val expenseID = CalculateExpenseFirebaseRepository().getNewExpenseID(expenseGroupModel.id)
        val splitMode = mSplitMode.text.toString().trim()
        val paidBy = mPaidBy.text.toString().trim()
        val divideAmong = mDivideAmong.text.toString().trim()
        val expenseDesc = mExpenseDescription.text.toString().trim()
        val expenseAmount = mExpenseAmount.text.toString().trim().toDouble()
        val memberCount = divideAmong.split(",").size-1
        val paidByID = expenseGroupModel.members_id_name_map.filterValues { it == paidBy.trim() }.keys.iterator().next()
        val splitAmongMembersMap = mutableMapOf<String, String>()
        for (member in divideAmong.split(",")){
            if (expenseGroupModel.members_id_name_map.filterValues { it == member.trim() }.keys.iterator().hasNext()){
                val memberID = expenseGroupModel.members_id_name_map.filterValues { it == member.trim() }.keys.iterator().next()
                splitAmongMembersMap[memberID] = member
            }
        }

        val memberPaymentCalcMap = mutableMapOf<String, Double>()
        for (member in divideAmong.split(",")){
            if (expenseGroupModel.members_id_name_map.filterValues { it == member.trim() }.keys.iterator().hasNext()){
                val memberID = expenseGroupModel.members_id_name_map.filterValues { it == member.trim() }.keys.iterator().next()
                if (memberID == paidByID){
                    memberPaymentCalcMap["$memberID-Lent"] = (expenseAmount/memberCount)
                }
                else{
                    memberPaymentCalcMap["$memberID-Borrowed"] = (expenseAmount/memberCount)
                }
            }
        }

        val expenseModel = ExpenseModel(
            expenseID,
            expenseDesc,
            expenseAmount,
            paidBy,
            0.0,
            0.0,
            splitMode,
            splitAmongMembersMap,
            memberCount,
            getTimestamp(),
            "",
            memberPaymentCalcMap,
            mutableMapOf(),
            paidByID,
            expenseGroupModel.name,
            expenseGroupModel.id
        )
        val addExpenseFragmentViewModel = ViewModelProvider(this)[AddExpenseFragmentViewModel::class.java]
        addExpenseFragmentViewModel._addNewExpenseToFirebaseFirestore(expenseModel)
        updateExpenseGroup(memberPaymentCalcMap, paidByID)
    }

    private fun updateExpenseGroup(memberPaymentCalcMap: MutableMap<String, Double>, paidByID: String){

        val addExpenseFragmentViewModel = ViewModelProvider(this)[AddExpenseFragmentViewModel::class.java]
        val groupPayStatusMap : MutableMap<String, MutableMap<String, Double>> = mutableMapOf()
        val tempMap: MutableMap<String, Double> = mutableMapOf()
        addExpenseFragmentViewModel._getMembersPayStatusFromGroup(expenseGroupModel.id,
            object : MyFirestoreInterface {
                override fun onExpenseGroupModelUpdateCallback(model: ExpenseGroupModel) {

                    for ((memberIDDB, payStatusDB) in model.members_payment_status) {
                        for ((memberIDCurr, payStatusCurr) in memberPaymentCalcMap){

                            if (memberIDCurr == memberIDDB){

                                if (memberIDDB.trim().split("-")[0] == paidByID){

                                    if (payStatusDB != 0.0){

                                        val amt = (payStatusDB + payStatusCurr)
                                        tempMap[memberIDDB] = amt
                                    }
                                    else {
                                        tempMap[memberIDDB] = payStatusCurr
                                    }
                                }
                                else {

                                    if (payStatusDB != 0.0){

                                        val amt = (payStatusDB + payStatusCurr)
                                        tempMap[memberIDDB] = amt
                                    }
                                    else {
                                        tempMap[memberIDDB] = payStatusCurr
                                    }
                                }
                            }
                        }
                    }
                    groupPayStatusMap["members_payment_status"] = tempMap
                    Log.d("TAG", "onExpenseGroupModelUpdateCallback: groupPayStatusMap $groupPayStatusMap")
                    addExpenseFragmentViewModel._updateMemberPaymentsToFirebaseFirestore(expenseGroupModel, groupPayStatusMap)
                }

                override fun onExpenseGroupModelUpdateLiveDataCallback(liveData: MutableLiveData<ExpenseGroupModel?>) {

                }
            })
        //findNavController().navigateUp()
    }

    private fun getTimestamp(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getDefault()
        return format.format(Date())
    }

    private fun setupWidgets(){
        if (view != null){
            mSplitMode = view!!.findViewById(R.id.split_mode)

            val splitModeAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, splitModeEntries)
            mSplitMode.setAdapter(splitModeAdapter)
            mSplitMode.keyListener = null

            mPaidBy = view!!.findViewById(R.id.paid_by)
            val paidByAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, paidByEntries)
            mPaidBy.setAdapter(paidByAdapter)
            mPaidBy.keyListener = null

            mDivideAmong = view!!.findViewById(R.id.divide_among)
            mDivideAmong.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
            val divideAmongAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, divideAmongEntries)
            mDivideAmong.setAdapter(divideAmongAdapter)
            mDivideAmong.threshold = 1

            mExpenseDescription = view!!.findViewById(R.id.expense_desc)
            mExpenseAmount = view!!.findViewById(R.id.expense_amount)
            mAddExpenseBtn = view!!.findViewById(R.id.add_expense)
        }
    }
}