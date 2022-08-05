package com.jrrobo.juniorrobo.view.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.snackbar.Snackbar
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.databinding.FragmentProfileBinding
import com.jrrobo.juniorrobo.network.EndPoints
import com.jrrobo.juniorrobo.viewmodel.FragmentProfileViewModel
import kotlinx.coroutines.launch
import java.io.File


class ProfileFragment : Fragment() {

    // view binding object
    private var _binding: FragmentProfileBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentProfileBinding
        get() = _binding!!

    // create the list of permission requests
    private var permissionsToRequest = mutableListOf<String>()

    // FragmentProfileViewModel to hold the state of the ProfileFragment
    private val viewModel: FragmentProfileViewModel by activityViewModels()

    // class name as TAG for logging or debugging purpose
    private val TAG: String = javaClass.simpleName

    // request code to handle the camera permission specifically
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    private var croppedPhotoUri: Uri? = null

    private var profilePictureFile: File? = null

    private val cropImageActivity = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            croppedPhotoUri = result.uriContent!!

//            profilePictureFile = File(croppedPhotoUri.path.toString())
            profilePictureFile = File(result.getUriFilePath(requireContext()).toString())
            binding.shapeableImageViewProfile.setImageURI(croppedPhotoUri)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // when the profile fragment is opened the edit texts should be disabled
        disableEditTexts()




        // after clicking on the Edit Profile button then only enable all the edit texts to enter the data into it
        binding.buttonProfileEditButton.setOnClickListener {
            enableEditTexts()

            // imageview for user profile
            binding.shapeableImageViewProfile.isClickable = true
            binding.shapeableImageViewProfile.isEnabled =true
            binding.shapeableImageViewProfile.setOnClickListener {
                viewModel.imageViewClickedLiveData.value = true
                // check the camera permission by the user and if not granted request the permission
                if (!hasCameraPermission()) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                            Manifest.permission.CAMERA
                        ),
                        CAMERA_PERMISSION_REQUEST_CODE
                    )
                } else {

                    // if the permission is granted launch the camera intent
                    cropImageActivity.launch(
                        options {
                            setGuidelines(CropImageView.Guidelines.ON)
                            setAutoZoomEnabled(true)
                            setImageSource(includeGallery = true, includeCamera = true)
                        }
                    )
                }
            }

            binding.buttonProfileUpdateButton.visibility = View.VISIBLE
            binding.buttonProfileEditButton.visibility = View.INVISIBLE
        }

        // primary key of the student initialised to -1
        var pkStudentId: Int = -1

        // launch coroutine for requesting the primary key and displaying the profile data
        lifecycleScope.launchWhenStarted {

            //TODO : changed here used if block
            Log.d(TAG, "onCreateView: ${viewModel.imageViewClickedLiveData.value.toString()}")

            // request the primary key
            viewModel.getPkStudentIdPreference().observe(requireActivity(), Observer {
                // assign the primary key from the data store preference
                Log.d(TAG, "onCreateView: i am pk observer and caliing getStudentProfile")
                pkStudentId = it
                viewModel.imageViewClickedLiveData.let {
                    if(it.value==null || it.value == false){
                        viewModel.getStudentProfile(pkStudentId)
                    }
                }

            })


            // call the GET request only when image view is not clicked
            viewModel.imageViewClickedLiveData.let {
                if (it.value == null || it.value == false) {
                    Log.d(TAG, "onCreateView: below is profileGetFlow")
                    // from the profile GET request flow, collect the events of the GET request to display the profile data
                    viewModel.profileGetFlow.collect {
                        when (it) {
                            is FragmentProfileViewModel.ProfileGetEvent.Loading -> {

                            }

                            is FragmentProfileViewModel.ProfileGetEvent.Failure -> {
                                Snackbar.make(
                                    binding.buttonProfileUpdateButton,
                                    "Couldn't get profile details",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }

                            // upon successful GET event populate the profile data
                            is FragmentProfileViewModel.ProfileGetEvent.Success -> {

                                // assign the data to all the edit texts
                                Log.d(TAG, "onCreateView: profileGetFlow called Glide")
                                populateProfileForm(it.parsedStudentProfileData)
                                viewModel.profileData.value = it.parsedStudentProfileData
                            }
                            else -> {
                                Unit
                            }
                        }
                    }
                }
            }
        }

        // upon clicking the update profile button disable the edit texts and the entered data will
        // as it is
        binding.buttonProfileUpdateButton.setOnClickListener {

            disableEditTexts()
            binding.buttonProfileUpdateButton.visibility = View.INVISIBLE
            binding.buttonProfileEditButton.visibility = View.VISIBLE

            Log.d(TAG, "onCreateView:imageViewClicked----> ${viewModel.imageViewClickedLiveData.value}")
            Log.d(TAG, "onCreateView:userImage----> ${viewModel.profileData.value?.UserImage}")

            viewModel.imageViewClickedLiveData.let {
                if(it.value==null || it.value == false){
                    // create an object for POST request, to update the user data
                    val profileUpdate =
                        StudentProfileData(
                            pkStudentId,
                            binding.editTextFirstName.text.toString(),
                            binding.editTextLastName.text.toString(),
                            binding.editTextEmail.text.toString(),
                            binding.editTextMobileNumber.text.toString(),
                            viewModel.profileData.value?.UserImage?:"default.jpg",
                            binding.editTextCity.text.toString()
                        )

                    updateProfile(profileUpdate)
                    viewModel.profileData.value = profileUpdate // if any field is changed but image not changed
                }
                else{
                    //"ddc276df-449a-475a-8be7-44d0f433d5b6.png" default image
                    // launch coroutine for uploading image

                    lifecycleScope.launch {
                        viewModel.uploadProfileImage(profilePictureFile)
                        viewModel.imageUploadFlow.collect{
                            when(it){
                                is FragmentProfileViewModel.ImageUploadEvent.Loading ->{

                                }
                                is FragmentProfileViewModel.ImageUploadEvent.Success ->{
                                    val profileUpdate =
                                        StudentProfileData(
                                            pkStudentId,
                                            binding.editTextFirstName.text.toString(),
                                            binding.editTextLastName.text.toString(),
                                            binding.editTextEmail.text.toString(),
                                            binding.editTextMobileNumber.text.toString(),
                                            it.hashedImageName,
                                            binding.editTextCity.text.toString()
                                        )

                                    Log.d(TAG, "onCreateView: The StudentProfileData object has image=${profileUpdate.UserImage}")
                                    Log.d(TAG, "onCreateView: making call to update Profile")
                                    updateProfile(profileUpdate)
                                    Log.d(TAG, "onCreateView: Successfully uploaded image: ${it.hashedImageName}")
                                    viewModel.profileData.value = profileUpdate // if any field is changed but image also changed
                                }
                                else ->{
                                    val profileUpdate =
                                        StudentProfileData(
                                            pkStudentId,
                                            binding.editTextFirstName.text.toString(),
                                            binding.editTextLastName.text.toString(),
                                            binding.editTextEmail.text.toString(),
                                            binding.editTextMobileNumber.text.toString(),
                                            viewModel.profileData.value?.UserImage?:"default.jpg",
                                            binding.editTextCity.text.toString()
                                        )
                                    updateProfile(profileUpdate)
                                    viewModel.profileData.value = profileUpdate // if any field is changed but image also changed
                                }
                            }
                        }

                    }
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: called")
    }



    private fun updateProfile(profileUpdate: StudentProfileData) {
        // launch the coroutine for updating the profile from the above data
        lifecycleScope.launch {

            // launch the update profile function
            viewModel.updateProfile(profileUpdate)

            // collect the flow of the Profile Update event
            viewModel.profileUpdateFlow.collect {
                when (it) {
                    is FragmentProfileViewModel.ProfileUpdateEvent.Loading -> {
                        Snackbar.make(
                            binding.buttonProfileUpdateButton,
                            "Please Wait",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    // upon successful update of the profile set the data store preference of profile created status to true
                    is FragmentProfileViewModel.ProfileUpdateEvent.Success -> {

                        Snackbar.make(
                            binding.buttonProfileUpdateButton,
                            "Profile updated successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, "updateProfile: profileUpdateFlow called Glide")
                        populateProfileForm(it.parsedStudentProfileData)
                        //after updating populate the edit text

                        viewModel.setProfileCreatedStatus(true)
                        disableImageClicks()
                    }
                    else -> {
                        Unit
                        disableImageClicks()
                    }

                }
            }
        }
    }

    // function which populates the profile edit texts
    private fun populateProfileForm(studentProfileData: StudentProfileData) {
        binding.editTextFirstName.setText(studentProfileData.FirstName)
        binding.editTextLastName.setText(studentProfileData.LastName)
        binding.editTextMobileNumber.setText(studentProfileData.Mobile)
        binding.editTextEmail.setText(studentProfileData.Email)
        binding.editTextCity.setText(studentProfileData.City)
        binding.textViewUserName.text =
            "${studentProfileData.FirstName} ${studentProfileData.LastName}"

        // load the image from Server
        lifecycleScope.launch {
                Log.d(TAG, "populateProfileForm: Glide called")
                Glide.with(binding.root)
                    .load(EndPoints.GET_IMAGE+studentProfileData.UserImage)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(binding.shapeableImageViewProfile)
        }
        
        
/*
        lifecycleScope.launch {
            viewModel.getImage(studentProfileData.UserImage)

            viewModel.imageGetFlow.collect{
                when (it) {
                    is FragmentProfileViewModel.ImageGetEvent.Loading -> {
                    }

                    // upon successful update of the profile set the data store preference of profile created status to true
                    is FragmentProfileViewModel.ImageGetEvent.Success -> {

                        val body: ResponseBody = it.responseBody
                        val bytes = body.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        binding.shapeableImageViewProfile.setImageBitmap(bitmap)
                    }
                    is FragmentProfileViewModel.ImageGetEvent.Failure-> {
//                        binding.shapeableImageViewProfile.setImageDrawable(ResourcesCompat.getDrawable(
//                            resources, R.drawable.ic_baseline_person_24,null))
                        //setting default person image when image is not loaded
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }

 */
    }

    private fun disableImageClicks(){
        viewModel.imageViewClickedLiveData.value = false
        binding.shapeableImageViewProfile.isClickable = false
        binding.shapeableImageViewProfile.isEnabled =false
        binding.shapeableImageViewProfile.setOnClickListener(null)
    }


    // For enabling the EditTexts to enter data on clicking the EditProfile Button
    private fun enableEditTexts() {
        binding.editTextFirstName.isEnabled = true
        binding.editTextLastName.isEnabled = true
        binding.editTextMobileNumber.isEnabled = true
        binding.editTextEmail.isEnabled = true
        binding.editTextCity.isEnabled = true
    }

    // For enabling the EditTexts to enter data on clicking the UpdateProfile Button
    private fun disableEditTexts() {
        binding.editTextFirstName.isEnabled = false
        binding.editTextLastName.isEnabled = false
        binding.editTextMobileNumber.isEnabled = false
        binding.editTextEmail.isEnabled = false
        binding.editTextCity.isEnabled = false
    }

    // to request for the camera permission if the camera permission is allowed for the app
    // to update the profile picture of the student profile
    private fun requestCameraPermission() {
        if (!hasCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
    }

    // function to check whether the app has camera permission or not
    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    // set the view binding object to null upon destroying the view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}