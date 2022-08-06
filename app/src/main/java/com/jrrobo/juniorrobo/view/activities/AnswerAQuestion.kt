package com.jrrobo.juniorrobo.view.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.ActivityAnswerAquestionBinding
import com.jrrobo.juniorrobo.viewmodel.ActivityAnswerAQuestionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * Activity for answering a particular question
 */
@AndroidEntryPoint
class AnswerAQuestion : AppCompatActivity() {

    private val TAG: String = javaClass.simpleName

    private lateinit var binding: ActivityAnswerAquestionBinding

    private val viewModel: ActivityAnswerAQuestionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerAquestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val questionItem = intent.extras?.getParcelable<QuestionItem>("question_item_for_answer")

        var pkStudentId: Int = -1
        lifecycleScope.launchWhenStarted {
            viewModel.getPkStudentIdPreference().observe(this@AnswerAQuestion, Observer {
                pkStudentId = it
                Log.d(TAG, pkStudentId.toString())
            })
        }

        binding.apply {
            textViewQuestionForAnswer.text = questionItem?.question
            textViewQuestionTagForAnswer.text = questionItem?.question_sub_text
        }

        Log.d(TAG, questionItem!!.id.toString())

        binding.buttonPostAnswer.setOnClickListener {
            viewModel.postAnswer(
                AnswerItem(
                    1,
                    binding.editTextAnswer.text.toString(),
                    null,
                    "null",
                    ""
                )
            )
        }
    }
}