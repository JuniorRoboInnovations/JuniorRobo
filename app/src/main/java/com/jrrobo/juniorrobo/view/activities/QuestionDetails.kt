package com.jrrobo.juniorrobo.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.ActivityQuestionDetailsBinding
import com.jrrobo.juniorrobo.view.adapter.AnswerItemAdapter
import com.jrrobo.juniorrobo.viewmodel.ActivityAnswerAQuestionViewModel
import dagger.hilt.android.AndroidEntryPoint

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
        viewModel.getAnswer(71)
        val questionItem = intent.extras!!.getParcelable<QuestionItem>("question_item")

        binding.apply {
            textViewQuestion.text = questionItem?.question
            textViewQuestionTag.text = questionItem?.question_sub_text
        }

        binding.buttonAnswerThis.setOnClickListener {
            val intent = Intent(this, AnswerAQuestion::class.java)
            intent.putExtra("question_item_for_answer", questionItem)
            startActivity(intent)
        }

        binding.buttonSeeAnswer.setOnClickListener {view: View ->
            val v = if (binding.answersRv.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.answersRv.visibility = v
            binding.buttonSeeAnswer.visibility = View.GONE
        }

        binding.answersRv.apply {
            viewModel.answers.observe(this@QuestionDetails, Observer {
                layoutManager = LinearLayoutManager(this@QuestionDetails)
                adapter = AnswerItemAdapter(it)

                if (it.isEmpty()) {
                    Log.d(TAG, "onViewCreated: rv empty")
                    viewModel.getAnswer(71)
                }

                Log.e(TAG, "onCreate: Adapter -> ${adapter.toString()}", )
            })


        }
    }

}