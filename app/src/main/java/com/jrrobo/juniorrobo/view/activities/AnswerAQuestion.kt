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
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.snackbar.Snackbar
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.answer.AnswerItemPost
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.ActivityAnswerAquestionBinding
import com.jrrobo.juniorrobo.network.EndPoints
import com.jrrobo.juniorrobo.viewmodel.ActivityAnswerAQuestionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * Activity for answering a particular question
 */
@AndroidEntryPoint
class AnswerAQuestion : AppCompatActivity() {

    private val TAG: String = javaClass.simpleName

    private lateinit var binding: ActivityAnswerAquestionBinding

    private val viewModel: ActivityAnswerAQuestionViewModel by viewModels()

    // request code for camera permission
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    // uri class variable for image pick and displaying the image and preview the picked image
    private lateinit var croppedPhotoUri: Uri

    private var answerImageFile: File? = null


    // CropImage contract (for Can Hub image cropper)
    // for handling the response of the image picker
    private val cropImageActivity = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            croppedPhotoUri = result.uriContent!!

            answerImageFile = File(result.getUriFilePath(this).toString())
            binding.cardviewAnswerImage.visibility = View.VISIBLE
        }
    }

    private lateinit var questionItemIntent: QuestionItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerAquestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val questionItem = intent.extras?.getParcelable<QuestionItem>("question_item")

        if (questionItem != null) {
            questionItemIntent = questionItem
        }

        var pkStudentId: Int = -1
        lifecycleScope.launchWhenStarted {
            viewModel.getPkStudentIdPreference().observe(this@AnswerAQuestion, Observer {
                pkStudentId = it
                Log.d(TAG, pkStudentId.toString())
            })
        }

        binding.apply {
            if (questionItem?.question.isNullOrEmpty()){
                questionMarkImage.visibility = View.GONE
                textViewQuestionForAnswer.visibility = View.GONE
            }
            if (questionItem?.question_sub_text.isNullOrEmpty()){
                materialTextView11.visibility = View.GONE
                textViewQuestionTagForAnswer.visibility = View.GONE
            }
            textViewQuestionForAnswer.text = questionItem?.question
            textViewQuestionTagForAnswer.text = questionItem?.question_sub_text

            if (questionItem?.image.isNullOrEmpty()) {
                binding.imageViewQuestion.visibility = View.GONE
            }
            else {
                binding.imageViewQuestion.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(binding.root)
                        .load(EndPoints.GET_IMAGE + "/question/" + questionItem?.image)
                        .into(binding.imageViewQuestion)
                }
            }
        }

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
        binding.imageButtonClearAnswerImage.setOnClickListener {
            binding.cardviewAnswerImage.visibility = View.GONE
            binding.answerImagePreview.visibility = View.GONE
        }

        binding.cardviewAnswerImage.setOnClickListener {
            binding.answerImagePreview.visibility = View.VISIBLE
            binding.answerImagePreview.setImageURI(croppedPhotoUri)

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

            binding.buttonPostAnswer.setOnClickListener {

                val answertext = binding.editTextAnswer.text.toString()

                if (answerImageFile != null) {
                    lifecycleScope.launch {
                        viewModel.postAnswerImage(answerImageFile!!)

                        //collect the hashed answer image name
                        viewModel.postAnswerImageEventFlow.collect {
                            when (it) {
                                is ActivityAnswerAQuestionViewModel.PostAnswerImageEvent.Loading -> {

                                }

                                is ActivityAnswerAQuestionViewModel.PostAnswerImageEvent.Failure -> {
                                    Snackbar.make(
                                        binding.editTextAnswer,
                                        "Couldn't upload the answer image!",
                                        Snackbar.LENGTH_LONG
                                    ).show()

                                    postAnswerItem(
                                        AnswerItemPost(
                                            answertext,
                                            pkStudentId,
                                            null,
                                            questionItem!!.id,
                                            null
                                        )
                                    )
                                }

                                // upon successful POST event
                                is ActivityAnswerAQuestionViewModel.PostAnswerImageEvent.Success -> {

                                    postAnswerItem(
                                        AnswerItemPost(
                                            answertext,
                                            pkStudentId,
                                            null,
                                            questionItem!!.id,
                                            //Answer image field to be added
                                            it.answerImagePostResponse
                                        )
                                    )
                                }
                                else -> {
                                    Unit
                                }
                            }
                        }
                    }
                } else {
                    if(answertext != ""){
                        postAnswerItem(
                            AnswerItemPost(
                                answertext,
                                pkStudentId,
                                null,
                                questionItem!!.id,
                                null
                            )
                        )
                    }
                    else{
                        Snackbar.make(
                            binding.editTextAnswer,
                            "Please add an answer first!",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                }
            }
        }

private fun postAnswerItem(answerItemPost: AnswerItemPost){
    viewModel.postAnswer(answerItemPost)
    lifecycleScope.launch {
        viewModel.postAnswerEventFlow.collect {
            when (it) {
                is ActivityAnswerAQuestionViewModel.PostAnswerItemEvent.Loading -> {

                }

                is ActivityAnswerAQuestionViewModel.PostAnswerItemEvent.Failure -> {
                    Snackbar.make(
                        binding.editTextAnswer,
                        "Couldn't post the answer!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                // upon successful POST event
                is ActivityAnswerAQuestionViewModel.PostAnswerItemEvent.Success -> {
                    binding.buttonPostAnswer.isEnabled = false
                    Snackbar.make(
                        binding.editTextAnswer,
                        "Success!! \nYou will see your answer once it is \n approved by our experts",
                        Snackbar.LENGTH_LONG
                    ).show()
                    clearTextFields()
                    Handler(Looper.getMainLooper()).postDelayed({

                        val intent = Intent(this@AnswerAQuestion, QuestionDetails::class.java)
                        intent.putExtra("question_item", questionItemIntent)
                        startActivity(intent)
                        this@AnswerAQuestion.finish()
                    }, 1000)
                    Log.d(
                        TAG,
                        "onCreate: Answer posted->${it.answerItemPostResponse}"
                    )

                }
                else -> {
                    Unit
                }
            }
        }
    }
}

private fun clearTextFields() {
    binding.editTextAnswer.text = null
}
// function to check whether the camera permission is granted by the user or not
private fun hasCameraPermission() =
    ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}