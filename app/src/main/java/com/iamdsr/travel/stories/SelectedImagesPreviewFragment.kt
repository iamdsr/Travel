package com.iamdsr.travel.stories

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.SelectedImagesRecyclerAdapter
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.SelectedImagesModel
import kotlinx.android.synthetic.main.fragment_selected_images_preview.*

class SelectedImagesPreviewFragment : Fragment(), RecyclerViewActionsInterface {

    private lateinit var selectedItemRecyclerAdapter: SelectedImagesRecyclerAdapter
    private lateinit var imageList: ArrayList<SelectedImagesModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_selected_images_preview, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        if (arguments != null){
            imageList = requireArguments().getParcelableArrayList("SELECTED_IMAGES_LIST")!!
            selectedItemRecyclerAdapter.submitList(imageList)
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val bundle = Bundle()
                bundle.putParcelableArrayList("SELECTED_IMAGES_LIST", imageList as ArrayList<out Parcelable?>?)
                findNavController().navigate(R.id.action_selectedImagesPreviewFragment_to_addNewPostFragment, bundle)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
    private fun initRecyclerView() {
        selected_images_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        selected_images_recycler_view.setHasFixedSize(true)
        selectedItemRecyclerAdapter = SelectedImagesRecyclerAdapter(this)
        selected_images_recycler_view.adapter = selectedItemRecyclerAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d("TAG", "onItemClick: position $position")
        imageList.removeAt(position)
        selectedItemRecyclerAdapter.notifyItemRemoved(position)
    }

}