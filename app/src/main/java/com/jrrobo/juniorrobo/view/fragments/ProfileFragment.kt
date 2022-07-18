package com.jrrobo.juniorrobo.view.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.snackbar.Snackbar
import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.databinding.FragmentProfileBinding
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

    private lateinit var croppedPhotoUri: Uri

    private lateinit var profilePictureFile: File

    private val cropImageActivity = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            croppedPhotoUri = result.uriContent!!

            profilePictureFile = File(croppedPhotoUri.path.toString())

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
            binding.buttonProfileUpdateButton.visibility = View.VISIBLE
            binding.buttonProfileEditButton.visibility = View.INVISIBLE
        }

        // primary key of the student initialised to -1
        var pkStudentId: Int = -1

        // launch coroutine for requesting the primary key and displaying the profile data
        lifecycleScope.launchWhenStarted {

            // request the primary key
            viewModel.getPkStudentIdPreference().observe(requireActivity(), Observer {

                // assign the primary key from the data store preference
                pkStudentId = it
                viewModel.getStudentProfile(pkStudentId)
            })

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
                        populateProfileForm(it.parsedStudentProfileData)
                    }
                    else -> {
                        Unit
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

            // create an object for POST request, to update the user data
            val profileUpdate = StudentProfileData(
                pkStudentId,
                binding.editTextFirstName.text.toString(),
                binding.editTextLastName.text.toString(),
                binding.editTextEmail.text.toString(),
                binding.editTextMobileNumber.text.toString(),
                "default.jpg",
                binding.editTextCity.text.toString()
            )

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

                            viewModel.setProfileCreatedStatus(true)
                        }
                        else -> {
                            Unit
                        }
                    }
                }
            }
        }

        // imageview for user profile
        binding.shapeableImageViewProfile.setOnClickListener {

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

        return binding.root
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