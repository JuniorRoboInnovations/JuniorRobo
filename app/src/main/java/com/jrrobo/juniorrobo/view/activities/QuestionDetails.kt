package com.jrrobo.juniorrobo.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.ActivityQuestionDetailsBinding
import com.jrrobo.juniorrobo.view.adapter.AnswerItemAdapter
import com.jrrobo.juniorrobo.viewmodel.ActivityAnswerAQuestionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionDetails : AppCompatActivity() {

    private val TAG: String = javaClass.simpleName

    private val viewModel: ActivityAnswerAQuestionViewModel by viewModels()

    /* private val list = listOf<AnswerItem>(
        AnswerItem(
            id = 72,
            "Hello Answer",
            null,
            student_image = "null",
            Date()
        ),
        AnswerItem(
            id = 72,
            "Hello Answer",
            null,
            student_image = "null",
            Date()
        ),
        AnswerItem(
            id = 72,
            "Hello Answer",
            null,
            student_image = "null",
            Date()
        ),
        AnswerItem(
            id = 72,
            "Hello Answer",
            null,
            student_image = "null",
            Date()
        ),
        AnswerItem(
            id = 72,
            "Hello Answer",
            null,
            student_image = "null",
            Date()
        )
    )*/

    // private val adapterRv = AnswerItemAdapter(list)

    // view binding object
    private lateinit var binding: ActivityQuestionDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Solution"
        viewModel.getAnswer(70)
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
                binding.answersRv.layoutManager = LinearLayoutManager(applicationContext)
                binding.answersRv.adapter = AnswerItemAdapter(it)
                if (it.isEmpty()) {
                    Log.d(TAG, "onViewCreated: rv empty")
                    viewModel.getAnswer(70)
                }
            })
        }
    }

}