package com.jrrobo.juniorrobo.view.activities

import android.animation.LayoutTransition
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.ActivityQuestionDetailsBinding
import com.jrrobo.juniorrobo.network.EndPoints
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
abstract class QuestionDetails : AppCompatActivity() {

    private val TAG: String = javaClass.simpleName

    private lateinit var croppedPhotoUri: Uri

    private lateinit var studentProfileData: StudentProfileData

    // view binding object
    private lateinit var binding: ActivityQuestionDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Solution"

        val questionItem = intent.extras!!.getParcelable<QuestionItem>("question_item")

        binding.apply {
            textViewQuestion.text = questionItem?.question
            textViewQuestionTag.text = questionItem?.question_sub_text
            // load the image from Server
            lifecycleScope.launch {

                Log.d(TAG, "populateProfileForm: Glide called")
                Glide.with(binding.root)
                    .load(EndPoints.GET_IMAGE+studentProfileData.UserImage)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(binding.profileImage)
            }

            binding.answerCardviewQuestionImage.setOnClickListener {

                var dialogImagePreview: AlertDialog? = null

                val builder: AlertDialog.Builder = AlertDialog.Builder(applicationContext)
                val customLayout: View = layoutInflater.inflate(R.layout.dialog_image_preview, null)
                customLayout.findViewById<ImageView>(R.id.imageview_dialog_image_preview)
                    .setImageURI(croppedPhotoUri)
                builder.setView(customLayout)

                builder.setPositiveButton("Cancel", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        dialogImagePreview!!.dismiss()
                    }
                })
                dialogImagePreview = builder.create()

                dialogImagePreview.show()
            }
        }

        binding.buttonAnswerThis.setOnClickListener {
            val intent = Intent(this, AnswerAQuestion::class.java)
            intent.putExtra("question_item_for_answer", questionItem)
            startActivity(intent)

            binding.answersRv.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)

            binding.buttonSeeAnswer.setOnClickListener {view: View ->
                val v = if (binding.answersRv.visibility == View.GONE) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                TransitionManager.beginDelayedTransition(binding.answersRv, AutoTransition())
                binding.answersRv.visibility = v
                binding.buttonSeeAnswer.visibility = View.GONE
            }
        }
    }

}