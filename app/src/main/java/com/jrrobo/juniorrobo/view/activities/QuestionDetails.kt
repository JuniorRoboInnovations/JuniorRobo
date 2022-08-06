package com.jrrobo.juniorrobo.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.ActivityQuestionDetailsBinding
import com.jrrobo.juniorrobo.view.adapter.AnswerItemAdapter
import com.jrrobo.juniorrobo.viewmodel.ActivityAnswerAQuestionViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.security.auth.login.LoginException

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

        val questionId = intent.extras!!.getParcelable<QuestionItem>("questionItem")
        if (questionId != null) {
            viewModel.getAnswer(questionId.id)
           // viewModel.getAnswer(93)

        }else{
            Log.e(TAG, "onCreate: ${questionId}", )
        }

        binding.apply {
            textViewQuestion.text = questionId?.question
            textViewQuestionTag.text = questionId?.question_sub_text
        }

        binding.buttonAnswerThis.setOnClickListener {
            val intent = Intent(this, AnswerAQuestion::class.java)
            intent.putExtra("question_item_for_answer", questionId)
            startActivity(intent)
        }
//        if () {
//            Toast.makeText(this@QuestionDetails,"No Answers available. Be the first to Answer",Toast.LENGTH_LONG)
//                .show()
//            binding.answersRv.visibility = View.GONE
//        }else{
//
//        }

        binding.answersRv.apply {
            viewModel.answers.observe(this@QuestionDetails, Observer {
                layoutManager = LinearLayoutManager(this@QuestionDetails)
                adapter = AnswerItemAdapter(it)

                if (it.isEmpty()) {
                    binding.answersRv.visibility = View.GONE
                    binding.noDataImage.visibility = View.VISIBLE
                    Toast.makeText(this@QuestionDetails,"No Answers available. Be the first to Answer",Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, "onViewCreated: rv empty")
                    if (questionId != null) {
                        viewModel.getAnswer(questionId.id)
                     //   viewModel.getAnswer(93)
                    }
                }
                else{
                    binding.answersRv.visibility = View.VISIBLE
                    binding.noDataImage.visibility = View.GONE

                }
            })


        }
    }

}