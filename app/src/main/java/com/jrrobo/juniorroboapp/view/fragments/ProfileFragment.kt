package com.jrrobo.juniorroboapp.view.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
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
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.data.profile.StudentProfileData
import com.jrrobo.juniorroboapp.databinding.FragmentProfileBinding
import com.jrrobo.juniorroboapp.network.EndPoints
import com.jrrobo.juniorroboapp.view.activities.MainActivity
import com.jrrobo.juniorroboapp.viewmodel.FragmentProfileViewModel
import com.jrrobo.juniorroboapp.viewmodel.FragmentQuestionsViewModel
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

    private val questionsViewModel: FragmentQuestionsViewModel by activityViewModels()

    // class name as TAG for logging or debugging purpose
    private val TAG: String = javaClass.simpleName

    // request code to handle the camera permission specifically
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    private var croppedPhotoUri: Uri? = null

    private var profilePictureFile: File? = null

    private var imageViewClicked: Boolean = false

    private var profileData: StudentProfileData? = null

    private val cropImageActivity = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            croppedPhotoUri = result.uriContent!!
            profilePictureFile = File(result.getUriFilePath(requireContext()).toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // when the profile fragment is opened the edit texts should be disabled
        disableEditTexts()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Upon click of the offer button
        binding.buttonOffer.setOnClickListener{
            lifecycleScope.launch {
                questionsViewModel.getOffer()
            }
            questionsViewModel.offerData.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    val dialogBinding = layoutInflater.inflate(R.layout.pop_up_layout, null)
                    val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
                    //            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(dialogBinding)
                    dialog.setCancelable(true)

                    val lp = WindowManager.LayoutParams()
                    lp.copyFrom(dialog.window!!.attributes)
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                    lp.gravity = Gravity.CENTER
                    lp.dimAmount = 0.7f

                    dialog.window!!.attributes = lp
                    dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                    dialog.show()

                    val title1 = dialogBinding.findViewById<TextView>(R.id.popup_title1)
                    val description1 = dialogBinding.findViewById<TextView>(R.id.popup_description1)

                    title1.text = it.title
                    description1.text = it.description

                    val cancelButton = dialogBinding.findViewById<ImageView>(R.id.image_clear_popup)
                    cancelButton.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            })
        }

        //Upon click of the logout button
        binding.profileLogoutImageButton.setOnClickListener {
            var dialogExit: AlertDialog? = null

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure to Logout ? ")

            builder.setPositiveButton("Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    dialogExit!!.dismiss()
                }
            })
            builder.setNegativeButton("Logout", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialogExit!!.dismiss()
                    viewModel.setOnBoardStatus(false)
                    viewModel.setOtpVerificationStatus(false)
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            })

            dialogExit = builder.create()
            dialogExit.show()
        }

        // after clicking on the Edit Profile button then only enable all the edit texts to enter the data into it
        binding.buttonProfileEditButton.setOnClickListener {
            enableEditTexts()
            enableImageClicks()

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
                    imageViewClicked = true
                    // if the permission is granted launch the camera intent
                    cropImageActivity.launch(
                        options {
                            setGuidelines(CropImageView.Guidelines.ON)
                            setAutoZoomEnabled(true)
                            setImageSource(includeGallery = true, includeCamera = true)
                        }
                    )
                    if (croppedPhotoUri != null) {
                        binding.shapeableImageViewProfile.setImageURI(croppedPhotoUri)
                    }
                }
            }

            binding.buttonProfileUpdateButton.visibility = View.VISIBLE
            binding.buttonProfileEditButton.visibility = View.INVISIBLE
        }

        // primary key of the student initialised to -1
        var pkStudentId: Int = -1

        // launch coroutine for requesting the primary key and displaying the profile data

        // request the primary key
        viewModel.getPkStudentIdPreference().observe(requireActivity(), Observer {
            // assign the primary key from the data store preference
            pkStudentId = it
        })

        if (!imageViewClicked) {
            viewModel.getStudentProfile(pkStudentId)
        }

        // call the GET request only when image view is not clicked
        lifecycleScope.launch {
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
                        profileData = it.parsedStudentProfileData
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }

        // upon clicking the update profile button disable the edit texts and the entered data will be populated
        binding.buttonProfileUpdateButton.setOnClickListener {

            disableEditTexts()
            binding.buttonProfileUpdateButton.visibility = View.INVISIBLE
            binding.buttonProfileEditButton.visibility = View.VISIBLE

            if (!imageViewClicked) {
                val profileUpdate =
                    StudentProfileData(
                        pkStudentId,
                        binding.editTextFirstName.text.toString(),
                        binding.editTextLastName.text.toString(),
                        binding.editTextEmail.text.toString(),
                        binding.editTextMobileNumber.text.toString(),
                        profileData?.userImage ?: "default.jpg",
                        binding.editTextCity.text.toString()
                    )
                updateProfile(profileUpdate)
                profileData = profileUpdate // if any field is changed but image not changed
            } else {
                Log.d(TAG, "onCreateView: ${profilePictureFile.toString()}")
                viewModel.uploadProfileImage(profilePictureFile)
                lifecycleScope.launch {
                    viewModel.imageUploadFlow.collect {
                        when (it) {
                            is FragmentProfileViewModel.ImageUploadEvent.Loading -> {

                            }
                            is FragmentProfileViewModel.ImageUploadEvent.Success -> {
                                var imageName = it.hashedImageName
                                if (imageName == "error") {
                                    Snackbar.make(
                                        binding.buttonProfileUpdateButton,
                                        "Couldn't upload image!",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    imageName = "default.jpg"
                                }
                                val profileUpdate = StudentProfileData(
                                    pkStudentId,
                                    binding.editTextFirstName.text.toString(),
                                    binding.editTextLastName.text.toString(),
                                    binding.editTextEmail.text.toString(),
                                    binding.editTextMobileNumber.text.toString(),
                                    imageName,
                                    binding.editTextCity.text.toString()
                                )
                                updateProfile(profileUpdate)
                                profileData =
                                    profileUpdate // if any field is changed along with image
                            }
                            is FragmentProfileViewModel.ImageUploadEvent.Failure -> {
                                Snackbar.make(
                                    binding.buttonProfileUpdateButton,
                                    "Couldn't upload image!",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            else -> {

                            }
                        }
                    }
                }

            }
        }
    }

    private fun updateProfile(profileUpdate: StudentProfileData) {
        // launch the update profile function
        viewModel.updateProfile(profileUpdate)

        // launch the coroutine for updating the profile from the above data
        lifecycleScope.launch {
            // collect the flow of the Profile Update event
            viewModel.profileUpdateFlow.collect {
                when (it) {
                    is FragmentProfileViewModel.ProfileUpdateEvent.Loading -> {

                    }
                    // upon successful update of the profile set the data store preference of profile created status to true
                    is FragmentProfileViewModel.ProfileUpdateEvent.Success -> {
                        Snackbar.make(
                            binding.buttonProfileUpdateButton,
                            "Profile updated successfully!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        populateProfileForm(it.parsedStudentProfileData)
                        viewModel.setProfileCreatedStatus(true)
                        disableImageClicks()
                    }
                    is FragmentProfileViewModel.ProfileUpdateEvent.Failure->{
                        disableImageClicks()
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }
    }

    // function which populates the profile edit texts
    private fun populateProfileForm(studentProfileData: StudentProfileData) {

        binding.editTextFirstName.setText(studentProfileData.firstName)
        binding.editTextLastName.setText(studentProfileData.lastName)
        binding.editTextMobileNumber.setText(studentProfileData.mobile)
        binding.editTextEmail.setText(studentProfileData.email)
        binding.editTextCity.setText(studentProfileData.city)
        if(studentProfileData.firstName.isNullOrEmpty() && studentProfileData.lastName.isNullOrEmpty()){
            binding.textViewUserName.text = "Junior Robo"
        }
        else{
            binding.textViewUserName.text = "${studentProfileData.firstName} ${studentProfileData.lastName}"
        }

        // load the image from Server
        lifecycleScope.launch {
            Glide.with(binding.root)
                .load(EndPoints.GET_IMAGE + "/student/" + studentProfileData.userImage)
                .error(R.drawable.app_logo_image)
                .into(binding.shapeableImageViewProfile)
        }
    }

    private fun enableImageClicks() {
        binding.shapeableImageViewProfile.isEnabled =true
    }

    private fun disableImageClicks(){
        imageViewClicked = false
        binding.shapeableImageViewProfile.isEnabled =false
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
//        _binding = null
    }
}