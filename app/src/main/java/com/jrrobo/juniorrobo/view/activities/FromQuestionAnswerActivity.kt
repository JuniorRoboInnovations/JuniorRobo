package com.jrrobo.juniorrobo.view.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.databinding.ActivityFromQuestionAnswerBinding
import com.jrrobo.juniorrobo.di.AppModule
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import com.jrrobo.juniorrobo.view.adapter.QuestionItemAdapter
import com.jrrobo.juniorrobo.view.fragments.LiveClassesFragment
import com.jrrobo.juniorrobo.view.fragments.ProfileFragment
import com.jrrobo.juniorrobo.view.fragments.QuestionAnswerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify

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

    override fun onBackPressed() {
        var dialogExit: AlertDialog? = null

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Exit ?")
        builder.setMessage("Are you sure to exit ? ")

        builder.setPositiveButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                dialogExit!!.dismiss()
            }
        })
        builder.setNegativeButton("Exit", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialogExit!!.dismiss()
                finish()
            }
        })

        dialogExit = builder.create()

        dialogExit.show()
    }
}