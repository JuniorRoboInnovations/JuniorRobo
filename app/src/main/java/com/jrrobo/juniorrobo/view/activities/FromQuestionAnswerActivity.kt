package com.jrrobo.juniorrobo.view.activities

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

    private val api = AppModule.provideCurrencyApi()
    private val MAIN = AppModule.provideDispatchers().main
    private val IO = AppModule.provideDispatchers().io

    private var originalList = GlobalScope.launch(MAIN) {
        withContext(IO) { api.getAllQuestionList(skip = 0, take = 10) }
    }
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
    //Testing the search function in question tab
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_search_questions,menu)

        val item =menu.findItem(R.menu.main_search_questions)
        val searchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterList(query) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    filterList(newText)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.filter -> {
                //Implement for filter functions
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun filterList(query : String ){

        GlobalScope.launch(MAIN){
            val response = withContext(IO){api.getAllQuestionList(skip = 0, take = 10, keyword = query)}
            var list = response.body()
            if(list.isNullOrEmpty()){
                //          originalList = list
                NetworkRequestResource.Success(response)
            }else {
                NetworkRequestResource.Error("No Results Found")
            }
            QuestionItemAdapter.notify()
        }
    }
}