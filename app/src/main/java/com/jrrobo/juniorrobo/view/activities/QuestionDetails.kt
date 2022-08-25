package com.jrrobo.juniorrobo.view.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.ActivityQuestionDetailsBinding
import com.jrrobo.juniorrobo.network.EndPoints
import com.jrrobo.juniorrobo.view.adapter.AnswerItemAdapter
import com.jrrobo.juniorrobo.viewmodel.ActivityAnswerAQuestionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionDetails : AppCompatActivity() {

    private val TAG: String = javaClass.simpleName

    private val viewModel: ActivityAnswerAQuestionViewModel by viewModels()

    // view binding object
    private lateinit var binding: ActivityQuestionDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Solution"

        val questionId = intent.extras?.getParcelable<QuestionItem>("question_item")

        if (questionId != null) {
            viewModel.getAnswer(questionId.id)
            /**
             * For checking if there is no answers in backend
             *  viewModel.getAnswer(93)
             */
        }else{
            Log.e(TAG, "onCreate: ${questionId}", )
        }

        binding.apply {
            textViewQuestion.text = questionId?.question
            textViewQuestionTag.text = questionId?.question_sub_text

            //Question Image to be updated here
            if (questionId?.image.isNullOrEmpty()) {
                answerQuestionImage.visibility = View.GONE
            }
            else {
                answerQuestionImage.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(root)
                        .load(EndPoints.GET_IMAGE + "/question/" + questionId!!.image)
                        .into(binding.answerQuestionImage)
                }
            }
            binding.answerQuestionImage.setOnClickListener {
                var dialogImagePreview: AlertDialog? = null

                val builder: AlertDialog.Builder = AlertDialog.Builder(this@QuestionDetails)
                val customLayout: View = LayoutInflater.from(this@QuestionDetails)
                    .inflate(R.layout.questionimage_layout_dialog, null)

                val imageView = customLayout.findViewById<ImageView>(R.id.questionImageView)
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(binding.root)
                        .load(EndPoints.GET_IMAGE + "/question/" + questionId!!.image)
                        .into(imageView)
                }
                builder.setView(customLayout)

                val cancelButton = customLayout.findViewById<ImageView>(R.id.cancelImageView)
                cancelButton.setOnClickListener {
                    dialogImagePreview?.dismiss()
                }

                dialogImagePreview = builder.create()

                dialogImagePreview.show()
            }
        }

        binding.buttonAnswerThis.setOnClickListener {
            val intent = Intent(this, AnswerAQuestion::class.java)
            intent.putExtra("question_item", questionId)
            startActivity(intent)
        }

        binding.answersRv.apply {
            viewModel.answers.observe(this@QuestionDetails, Observer {

                layoutManager = LinearLayoutManager(this@QuestionDetails)
                adapter = AnswerItemAdapter(it)

                if (it.isEmpty()) {

                    binding.noDataText.visibility = View.VISIBLE
                        if (questionId != null) {
                            viewModel.getAnswer(questionId.id)
                            /**
                             * For checking if there is no answers in backend
                             *  viewModel.getAnswer(93)
                             */
                        }
                    }
                else{
                    binding.answersRv.visibility = View.VISIBLE
                }
                if (it.size >= 5) {
                    binding.buttonAnswerThis.isEnabled = false
                    Snackbar.make(
                        binding.answersRv,
                        "Sorry! Couldn't post more answers",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            })
        }
    }
}