package com.jrrobo.juniorrobo.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.FragmentQuestionAnswerBinding
import com.jrrobo.juniorrobo.view.activities.AskQuestionActivity
import com.jrrobo.juniorrobo.view.activities.QuestionDetails
import com.jrrobo.juniorrobo.view.adapter.QuestionItemAdapter
import com.jrrobo.juniorrobo.viewmodel.FragmentQuestionsViewModel

class QuestionAnswerFragment : Fragment(), QuestionItemAdapter.OnQuestionItemClickListener {

    // view binding object
    private var _binding: FragmentQuestionAnswerBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentQuestionAnswerBinding
        get() = _binding!!

    // view model for this fragment
    private val viewModel: FragmentQuestionsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding object
        _binding = FragmentQuestionAnswerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = QuestionItemAdapter(this@QuestionAnswerFragment)

        viewModel.getQuestionCategories()

        viewModel.questionCategoriesLiveData.observe(requireActivity(), Observer {
            val listOfQuestionCategories: List<QuestionCategoryItem> = it.items
            binding.apply {
                var chip: Chip
                val chipGroup = binding.chipGroupQuestionCategoriesChips

                var allQuestionChipId: Int = 1
                for (questionCategory in listOfQuestionCategories) {
                    chip = Chip(requireContext())
                    chip.text = questionCategory.categoryTitle
                    chipGroup.addView(chip)
                    if (questionCategory.categoryTitle == "All") {
                        allQuestionChipId = chip.id
                    }
                }
                chipGroup.check(allQuestionChipId)
            }
        })

        binding.apply {
            rvQuestionsList.setHasFixedSize(true)
            rvQuestionsList.adapter = adapter
            rvQuestionsList.layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.questions.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        // handle the FAB to open the AskQuestionActivity
        binding.fabAskQuestion.setOnClickListener {
            val intent = Intent(requireActivity(), AskQuestionActivity::class.java)
            startActivity(intent)
        }
    }

    // set the view binding object to null upon destroying the view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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