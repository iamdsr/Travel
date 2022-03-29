package com.iamdsr.travel.calculateExpenses

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.SearchMemberRecyclerAdapter
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.UserModel
import com.iamdsr.travel.viewModels.SearchMemberFragmentViewModel
import java.util.*


class SearchMemberFragment : Fragment(), RecyclerViewActionsInterface{

    // Widgets
    private lateinit var mSearchUserRecyclerView: RecyclerView
    private lateinit var mSearchText: EditText

    // Utils
    private lateinit var searchedUserList: List<UserModel>
    private lateinit var searchMemberRecyclerAdapter: SearchMemberRecyclerAdapter
    private var groupID: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_member, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        initRecyclerView()
        if (arguments!=null){
            groupID = arguments!!.getString("EXPENSE_GROUP_ID","")
        }
        Log.d("TAG", "onItemClick: $groupID")
        mSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchUsers(s.toString().lowercase(Locale.getDefault()))
            }

            override fun afterTextChanged(s: Editable) {
                //searchUsers(s.toString().lowercase(Locale.getDefault()))
            }
        })
    }

    private fun searchUsers(searchText: String) {
        if (!TextUtils.isEmpty(searchText)){
            Log.d("TAG", "searchUsers: Search text : $searchText")
            val searchMemberFragmentViewModel = ViewModelProvider(requireActivity())[SearchMemberFragmentViewModel::class.java]
            searchMemberFragmentViewModel._getSearchedUsersFromFirebaseFirestore(searchText).observe(requireActivity(), androidx.lifecycle.Observer {
                searchedUserList = it
                searchMemberRecyclerAdapter.submitList(it)
                Log.d("TAG", "searchUsers: Submit ist $it")
            })
        }
        else {
            searchMemberRecyclerAdapter.submitList(listOf())
        }
    }


    private fun setupWidgets() {
        if (view!=null) {
            mSearchUserRecyclerView = view!!.findViewById(R.id.search_recycler_view)
            mSearchText = view!!.findViewById(R.id.search_text)
        }
    }

    private fun setDialogToAddMember(userModel: UserModel) {

        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.layout_dialog_confirm_cancel, null)
        val mDialogTitle  = dialogView.findViewById<View>(R.id.title) as TextView
        val mDialogDesc  = dialogView.findViewById<View>(R.id.desc) as TextView
        val mDialogCancelBtn  = dialogView.findViewById<View>(R.id.cancel) as Button
        val mDialogConfirmBtn  = dialogView.findViewById<View>(R.id.confirm) as Button

        mDialogTitle.setText(R.string.add_member_to_group)
        mDialogDesc.setText(R.string.add_member_desc)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        mDialogCancelBtn.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        mDialogConfirmBtn.setOnClickListener(View.OnClickListener {
            val addMemberMap: MutableMap<String, Any> = HashMap()
            val addIDMemberMap: MutableMap<String, MutableMap<String, Any>> = HashMap()
            val addMemberPayStatusMap: MutableMap<String, MutableMap<String, Any>> = HashMap()
            val iDNameMap:  MutableMap<String, Any> = HashMap()
            val namePayStatMap:  MutableMap<String, Any> = HashMap()
            val searchMemberFragmentViewModel = ViewModelProvider(requireActivity())[SearchMemberFragmentViewModel::class.java]
            addMemberMap["members_name"] = FieldValue.arrayUnion(userModel.full_name)
            iDNameMap[userModel.id] = userModel.full_name
            namePayStatMap[userModel.full_name] = ""
            addIDMemberMap["members_id_name_map"] = iDNameMap
            addMemberPayStatusMap["members_payment_status"] = namePayStatMap
            searchMemberFragmentViewModel._addMemberToExpenseGroupFirebaseFirestore(groupID, addMemberMap, addIDMemberMap, addMemberPayStatusMap)
            dialog.dismiss()
        })

    }

    private fun initRecyclerView() {
        mSearchUserRecyclerView.layoutManager = LinearLayoutManager(context)
        mSearchUserRecyclerView.setHasFixedSize(true)
        searchMemberRecyclerAdapter = SearchMemberRecyclerAdapter(this)
        mSearchUserRecyclerView.adapter = searchMemberRecyclerAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (FirebaseAuth.getInstance().currentUser?.uid != searchedUserList[position].id){
            setDialogToAddMember(searchedUserList[position])
        }
    }

}
