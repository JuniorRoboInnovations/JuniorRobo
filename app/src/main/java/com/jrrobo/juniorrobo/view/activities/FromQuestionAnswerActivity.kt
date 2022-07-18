package com.jrrobo.juniorrobo.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.databinding.ActivityFromQuestionAnswerBinding
import com.jrrobo.juniorrobo.view.fragments.LiveClassesFragment
import com.jrrobo.juniorrobo.view.fragments.ProfileFragment
import com.jrrobo.juniorrobo.view.fragments.QuestionAnswerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FromQuestionAnswerActivity : AppCompatActivity() {

    // TAG for logging purpose
    private val TAG: String = javaClass.simpleName

    // view binding object
    private lateinit var binding: ActivityFromQuestionAnswerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFromQuestionAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // handle the bottom navigation item click listener
        binding.mainBottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {

                // from the bottom navigation if the q&a button is clicked
                // replace the fragment with QuestionAnswerFragment.kt
                R.id.bottom_menu_question_answer -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(
                            R.id.from_question_answer_navigation_container,
                            QuestionAnswerFragment()
                        )
                        supportFragmentManager.popBackStack()
                        commit()
                    }
                }

                R.id.bottom_menu_live_classes -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(
                            R.id.from_question_answer_navigation_container,
                            LiveClassesFragment()
                        )
                        supportFragmentManager.popBackStack()
                        commit()
                    }
                }

                // from the bottom navigation if the Profile button is clicked
                // replace the fragment with ProfileFragment.kt
                R.id.bottom_menu_profile -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(
                            R.id.from_question_answer_navigation_container,
                            ProfileFragment()
                        )
                        supportFragmentManager.popBackStack()
                        commit()
                    }
                }
            }
            true
        }
    }
}