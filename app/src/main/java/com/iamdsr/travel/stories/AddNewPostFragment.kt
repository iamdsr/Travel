package com.iamdsr.travel.stories

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iamdsr.travel.R
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.SelectedImagesModel
import kotlinx.android.synthetic.main.fragment_add_new_post.*


class AddNewPostFragment : Fragment(), RecyclerViewActionsInterface {

    // utils
    //
    private var imageList: ArrayList<SelectedImagesModel> = arrayListOf()

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent? = it.data
            imageList.clear()
            if (data?.clipData != null) {
                val count = data.clipData?.itemCount ?: 0
                for (i in 0 until count) {
                    val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                    imageList.add(SelectedImagesModel("",imageUri,""))
                    if (i == 0){
                        selected_images_view.setImageURI(imageUri)
                    }
                }
                more_images.visibility = View.VISIBLE
                more_images.text = requireContext().resources.getString(R.string._2_more, count-1)
            }
            else if (data?.data != null) {
                more_images.visibility = View.INVISIBLE
                val imageUri: Uri? = data.data
                imageList.add(SelectedImagesModel("",imageUri,""))
                selected_images_view.setImageURI(imageUri)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null){
            imageList = requireArguments().getParcelableArrayList("SELECTED_IMAGES_LIST")!!
            Log.d("TAG", "onViewCreated: Image list ${imageList.size}")
            selected_images_view.setImageURI(imageList[0].uri)
            more_images.text = requireContext().resources.getString(R.string._2_more, (imageList.size)-1)
            more_images.visibility = View.VISIBLE
        }
        more_images.setOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList("SELECTED_IMAGES_LIST", imageList as ArrayList<out Parcelable?>?)
            findNavController().navigate(R.id.action_addNewPostFragment_to_selectedImagesPreviewFragment, bundle)
        })

        add_photos.setOnClickListener(View.OnClickListener {
            checkPermissionsAndPrepareUpload(requireContext())
        })
    }



    private fun checkPermissionsAndPrepareUpload(context: Context){
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                getContent.launch(intent)
            }
            else -> {
                requireActivity().requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }
        }
    }

    override fun onItemClick(view: View, position: Int) {

    }
}