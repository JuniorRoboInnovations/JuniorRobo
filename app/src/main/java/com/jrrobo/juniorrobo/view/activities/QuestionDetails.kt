package com.jrrobo.juniorrobo.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.ActivityQuestionDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionDetails : AppCompatActivity() {

    // view binding object
    private lateinit var binding: ActivityQuestionDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Can you answer this?"

        val questionItem = intent.extras!!.getParcelable<QuestionItem>("question_item")

        binding.apply {
            textViewQuestion.text = questionItem?.question
            textViewQuestionTag.text = questionItem?.questionSubtext
        }

        binding.buttonAnswerThis.setOnClickListener {
            val intent = Intent(this, AnswerAQuestion::class.java)
            intent.putExtra("question_item_for_answer", questionItem)
            startActivity(intent)
        }
    }
}