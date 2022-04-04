package com.iamdsr.travel.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.iamdsr.travel.R
import com.iamdsr.travel.interfaces.UsersFirestoreInterface
import com.iamdsr.travel.models.UserModel
import com.iamdsr.travel.viewModels.UserProfileViewModel
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.HashMap


class UpdateProfileFragment : Fragment() {

    // Widgets
    private lateinit var mChooseImage: Button
    private lateinit var mUserImage: CircleImageView
    private lateinit var mProgress: ProgressBar
    private lateinit var mUsername: TextInputEditText
    private lateinit var mFullName: TextInputEditText

    // Utils
    private lateinit var storageRef: StorageReference
    private val cropActivityResultContract = object : ActivityResultContract<List<Uri>,Uri?>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val uCropActivity = UCrop
                .of(input[0], input[1])
                .withAspectRatio(1f, 1f)
            return uCropActivity!!.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.let { UCrop.getOutput(it) }
        }


    }
    private val cropImage = registerForActivityResult(cropActivityResultContract){
        uploadImage(it!!)
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
        it.let { uri ->
            val outputUri = File(context?.filesDir, "croppedImage.jpg").toUri()
            val listUri = listOf(uri!!, outputUri)
            cropImage.launch(listUri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        setupFirebaseStorage()
        prepareUserDataToDisplay()
        mChooseImage.setOnClickListener(View.OnClickListener {
            checkPermissionsAndPrepareUpload(context!!)
        })
    }

    private fun prepareUserDataToDisplay() {

        val userProfileViewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
        userProfileViewModel._getUserDetails(FirebaseAuth.getInstance().currentUser!!.uid, object : UsersFirestoreInterface{
            override fun onUserDataAdded(model: UserModel) {

            }

            override fun onUserDataUpdated(model: UserModel) {
                if (context != null){
                    Glide
                        .with(context!!)
                        .load(model.user_profile_image_url)
                        .placeholder(R.drawable.placeholder_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mUserImage)
                }
                mUsername.setText( model.username)
                mFullName.setText(model.full_name)
            }

        })

    }

    private fun setupFirebaseStorage() {
        storageRef = FirebaseStorage.getInstance().reference
    }

    private fun checkPermissionsAndPrepareUpload(context: Context){
        when (PackageManager.PERMISSION_GRANTED) {
            checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // You can use the API that requires the permission.
                getContent.launch("image/*")
            }
            else -> {
                requireActivity().requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }
        }
    }
    private fun uploadImage(imageUri: Uri) {
        mProgress.visibility = View.VISIBLE
        val filePath = storageRef.child("User_Profile_Images/${FirebaseAuth.getInstance().currentUser!!.uid}.jpg")
        val uploadTask = filePath.putFile(imageUri)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                filePath.downloadUrl.addOnSuccessListener {
                    val userProfileViewModel = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
                    val userMap: MutableMap<String, String> = HashMap()
                    userMap["user_profile_image_url"] = it.toString()
                    userProfileViewModel._updateUserProfileImage(FirebaseAuth.getInstance().currentUser!!.uid, userMap)
                    if (context != null){
                        Glide
                            .with(context!!)
                            .load(it)
                            .placeholder(R.drawable.placeholder_image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mUserImage)
                    }
                    mProgress.visibility = View.GONE
                }
            } else {
                // Handle failures
                // ...
            }
        }
    }

    private fun setupWidgets() {
        if (view!=null){
            mChooseImage = view!!.findViewById(R.id.update_profile_photo)
            mUserImage = view!!.findViewById(R.id.user_image)
            mProgress = view!!.findViewById(R.id.progress_horizontal)
            mUsername = view!!.findViewById(R.id.user_name)
            mFullName = view!!.findViewById(R.id.user_full_name)

        }

    }

}