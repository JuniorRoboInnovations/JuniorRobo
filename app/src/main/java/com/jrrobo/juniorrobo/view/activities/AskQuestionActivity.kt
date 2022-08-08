package com.jrrobo.juniorrobo.view.activities

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.snackbar.Snackbar
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemToAsk
import com.jrrobo.juniorrobo.databinding.ActivityAskQuestionBinding
import com.jrrobo.juniorrobo.viewmodel.ActivityAskQuestionActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AskQuestionActivity : AppCompatActivity() {

    // TAG for logging purpose
    private val TAG: String = javaClass.simpleName

    // view binding object
    private lateinit var binding: ActivityAskQuestionBinding

    // view model of this activity
    private val viewModel: ActivityAskQuestionActivityViewModel by viewModels()

    // request code for camera permission
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    // request code for read and write permission
    private val READ_WRITE_EXTERNAL_PERMISSION_REQUEST_CODE = 200

    // uri class variable for image pick and displaying the image and preview the picked image
    private lateinit var croppedPhotoUri: Uri

    // uri for storing the path of the file picked by user to preview this in future if needed by the user
    private var _pickedFileUri: Uri? = null

    // non null version of the picked file uri
    private val pickedFileUri
        get() = _pickedFileUri!!

    // map to convert category name to category id
    private val catNameToCatIdMap: HashMap<String,Int> = HashMap()



    // CropImage contract (for Can Hub image cropper)
    // for handling the response of the image picker
    private val cropImageActivity = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            croppedPhotoUri = result.uriContent!!

//            binding.setImageURI(croppedPhotoUri)

            binding.cardviewQuestionImage.visibility = View.VISIBLE
        }
    }

    private val attachFileActivity =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it != null) {
                _pickedFileUri = it.data?.data
                binding.cardviewQuestionFile.visibility = View.VISIBLE
                Log.d(TAG, pickedFileUri.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAskQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var pkStudentId: Int = -1
        lifecycleScope.launchWhenStarted {
            viewModel.getPkStudentIdPreference().observe(this@AskQuestionActivity, Observer {
                pkStudentId = it
            })
        }

        // set the invisible action bar to visible and title
        supportActionBar?.title = "Ask Question"

        // take picture button to launch the image picker with crop feature
        binding.buttonTakePicture.setOnClickListener {

            // request the camera cropper intent
            if (!hasCameraPermission()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {

                // if the camera permission is granted launch the camera intent
                cropImageActivity.launch(
                    options {
                        setGuidelines(CropImageView.Guidelines.ON)
                        setAutoZoomEnabled(true)
                        setImageSource(includeGallery = true, includeCamera = true)
                    }
                )
            }
        }

        binding.imageButtonClearQuestionImage.setOnClickListener {
            binding.cardviewQuestionImage.visibility = View.GONE
        }

        binding.imageButtonClearQuestionFile.setOnClickListener {
            binding.cardviewQuestionFile.visibility = View.GONE
        }

        binding.cardviewQuestionImage.setOnClickListener {
            binding.previewImageTest.visibility = View.VISIBLE
            binding.previewImageTest.setImageURI(croppedPhotoUri)

            var dialogImagePreview: AlertDialog? = null

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val customLayout: View = layoutInflater.inflate(R.layout.dialog_image_preview, null)
            customLayout.findViewById<ImageView>(R.id.imageview_dialog_image_preview)
                .setImageURI(croppedPhotoUri)
            builder.setView(customLayout)

            builder.setPositiveButton("Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    dialogImagePreview!!.dismiss()
                }
            })

            builder.setNegativeButton("Change Image", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    cropImageActivity.launch(
                        options {
                            setGuidelines(CropImageView.Guidelines.ON)
                            setAutoZoomEnabled(true)
                            setImageSource(includeGallery = true, includeCamera = true)
                        }
                    )
                }
            })

            dialogImagePreview = builder.create()

            dialogImagePreview.show()
        }

        // attach file button to launch the file picker with permission
        binding.buttonAttachFile.setOnClickListener {

            // request read and write permissions before launching the file picker intent
            if (!(hasReadExternalStoragePermission() && hasWriteExternalStoragePermission())) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    READ_WRITE_EXTERNAL_PERMISSION_REQUEST_CODE
                )
            } else {
                attachFileActivity.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "application/pdf"
                })
            }
        }

        binding.cardviewQuestionFile.setOnClickListener {
            val objIntent = Intent(Intent.ACTION_VIEW)
            objIntent.setDataAndType(pickedFileUri, "application/pdf")
            objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(objIntent)
        }

        binding.buttonPostQuestion.setOnClickListener {
            Log.d(TAG, "onCreate: Student pkID->${pkStudentId}")
            lifecycleScope.launch {
                viewModel.postQuestionItem(
                    QuestionItemToAsk(
                        binding.editTextQuestion.text.toString(),
                        binding.autoCompleteTextView.text.toString(),
                        "All",
                        pkStudentId,
                        null,
                        catNameToCatIdMap[binding.autoCompleteTextView.text.toString()]?:13
                    )
                )
                viewModel.postQuestionEventFlow.collect {
                    when (it) {
                        is ActivityAskQuestionActivityViewModel.PostQuestionItemEvent.Loading -> {

                        }

                        is ActivityAskQuestionActivityViewModel.PostQuestionItemEvent.Failure -> {
                            Snackbar.make(
                                binding.editTextQuestion,
                                "Couldn't post the question!",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        // upon successful POST event
                        is ActivityAskQuestionActivityViewModel.PostQuestionItemEvent.Success -> {
                            Snackbar.make(
                                binding.editTextQuestion,
                                "Successfully posted the question!",
                                Snackbar.LENGTH_LONG
                            ).show()
                            clearTextFields()
                            Handler(Looper.getMainLooper()).postDelayed({
                                // go to fromQuestionAnswerActivity
                                val intent = Intent(this@AskQuestionActivity, FromQuestionAnswerActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                this@AskQuestionActivity.finish()
                            }, 3000)
                            Log.d(TAG, "onCreate: Question posted->${it.questionItemPostResponse.toString()}")

                        }
                        else -> {
                            Unit
                        }
                    }
                }
            }
        }
    }

    private fun clearTextFields() {
        binding.editTextQuestion.text = null
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            Log.d(TAG, "onViewCreated: calling getQuestionCategories")
            viewModel.getQuestionCategories()
        }
        val categoryList : ArrayList<String> = ArrayList()
        viewModel.questionCategoriesLiveData.observe(this, Observer {
            for(categoryItem in it){
                catNameToCatIdMap[categoryItem.categoryTitle] = categoryItem.pkCategoryId
                categoryList.add(categoryItem.categoryTitle)
            }
            val dropDownListAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,categoryList)
            binding.autoCompleteTextView.setAdapter(dropDownListAdapter)
        })

    }


    // function to check whether the camera permission is granted by the user or not
    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    // function to check whether the read external storage permission is granted by the user or not
    private fun hasReadExternalStoragePermission() = ActivityCompat.checkSelfPermission(
        this, Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    // function to check whether the write external permission is granted by the user or not
    private fun hasWriteExternalStoragePermission() =
        ActivityCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

//    // perform operations on permissions granted or denied
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray,
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
//            Log.d(TAG, permissions[0])
//        }
//
//        if (requestCode == READ_WRITE_EXTERNAL_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
//            Log.d(TAG, permissions[1])
//        }
//    }


}