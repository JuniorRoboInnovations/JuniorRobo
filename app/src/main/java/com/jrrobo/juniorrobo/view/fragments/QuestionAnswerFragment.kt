package com.jrrobo.juniorrobo.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.FragmentQuestionAnswerBinding
import com.jrrobo.juniorrobo.di.AppModule
import com.jrrobo.juniorrobo.view.activities.AskQuestionActivity
import com.jrrobo.juniorrobo.view.activities.QuestionDetails
import com.jrrobo.juniorrobo.view.adapter.QuestionItemAdapter
import com.jrrobo.juniorrobo.view.adapter.QuestionItemRvAdapter
import com.jrrobo.juniorrobo.viewmodel.FragmentQuestionsViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class QuestionAnswerFragment : Fragment(), QuestionItemAdapter.OnQuestionItemClickListener {

    // TAG for logging purpose
    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentQuestionAnswerBinding? = null
    val adapter = QuestionItemAdapter
    private val api = AppModule.provideCurrencyApi()
    private val MAIN = AppModule.provideDispatchers().main
    private val IO = AppModule.provideDispatchers().io

    private var originalList = GlobalScope.launch(MAIN) {
        withContext(IO) { api.getAllQuestionList(skip = 0, take = 10) }
    }

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentQuestionAnswerBinding?
        get() = _binding

    // view model for this fragment
    private val viewModel: FragmentQuestionsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding object
        _binding = FragmentQuestionAnswerBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // making the network calls with coroutines
        lifecycleScope.launch {
            Log.d(TAG, "onViewCreated: calling getQuestionCategories")
            viewModel.getQuestionCategories()

//            Log.d(TAG, "onViewCreated: calling getAllQuestions")
//            viewModel.getQuestions(2)

            Log.d(TAG, "onViewCreated: calling getAllQuestionsWithoutPaging")
            viewModel.getQuestionsWithoutPaging(null)
        }

        viewModel.questionCategoriesLiveData.observe(requireActivity(), Observer {
            val listOfQuestionCategories: List<QuestionCategoryItem> = it
            binding?.apply {

                context?.let {
                    var chip: Chip
                    val chipGroup = binding?.chipGroupQuestionCategoriesChips

                    var allQuestionChipId: Int = 1
                    for (questionCategory in listOfQuestionCategories) {
                        chip = Chip(context)
                        chip.text = questionCategory.categoryTitle
                        chipGroup?.addView(chip)
                        if (questionCategory.categoryTitle == "All") {
                            allQuestionChipId = chip.id
                        }
                    }
                    chipGroup?.check(allQuestionChipId)
                }

            }
        })

//        val adapter = QuestionItemAdapter(this@QuestionAnswerFragment)
//        binding?.apply {
//            rvQuestionsList.setHasFixedSize(true)
//            rvQuestionsList.adapter = adapter
//            rvQuestionsList.layoutManager = LinearLayoutManager(requireContext())
//        }
//
//        viewModel.questions.observe(viewLifecycleOwner) {
//            adapter.submitData(viewLifecycleOwner.lifecycle, it)
//        }


        with(_binding?.rvQuestionsList) {
            this?.layoutManager = LinearLayoutManager(requireContext())

            this?.adapter = QuestionItemRvAdapter { questionItem ->
                val intent = Intent(requireContext(), QuestionDetails::class.java)
                intent.putExtra("question_item", questionItem)
                startActivity(intent)
            }
        }

        //paging data
        viewModel.questionsWithoutPaging.observe(viewLifecycleOwner, Observer {
            (binding?.rvQuestionsList?.adapter as QuestionItemRvAdapter).submitList(it)
            // if empty fetch from network
            if (it.isEmpty()) {
                Log.d(TAG, "onViewCreated: rv empty")
                viewModel.getQuestionsWithoutPaging(null)
            }
        })
        //paging data


        // handle the FAB to open the AskQuestionActivity
        binding?.fabAskQuestion?.setOnClickListener {
            val intent = Intent(requireActivity(), AskQuestionActivity::class.java)
            startActivity(intent)
        }

        //Search View
        binding?.questionsSearchView?.isSubmitButtonEnabled = true
        binding?.questionsSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query.let {
                    searchUsers(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText.let {
                    searchUsers(it)
                }
                return true
            }

        })
    }

    private fun searchUsers(newText: String?) {

    }
// set the view binding object to null upon destroying the view
//    override fun onDestroyView() {
//        super.onDestroyView()
////        _binding = null
//    }

//    override fun onDetach() {
//        super.onDetach()
//        _binding = null
//    }

override fun onItemClick(questionItem: QuestionItem) {
    val intent = Intent(requireContext(), QuestionDetails::class.java)
    intent.putExtra("question_item", questionItem)
    startActivity(intent)
}
}