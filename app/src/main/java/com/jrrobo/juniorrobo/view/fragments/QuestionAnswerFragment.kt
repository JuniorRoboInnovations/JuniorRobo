package com.jrrobo.juniorrobo.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.FragmentQuestionAnswerBinding
import com.jrrobo.juniorrobo.view.activities.AskQuestionActivity
import com.jrrobo.juniorrobo.view.activities.QuestionDetails
import com.jrrobo.juniorrobo.view.adapter.QuestionItemAdapter
import com.jrrobo.juniorrobo.view.adapter.QuestionItemRvAdapter
import com.jrrobo.juniorrobo.viewmodel.FragmentQuestionsViewModel
import kotlinx.coroutines.launch

class QuestionAnswerFragment : Fragment(), QuestionItemAdapter.OnQuestionItemClickListener {

    // TAG for logging purpose
    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentQuestionAnswerBinding? = null

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

        var questions: List<QuestionItem>? = null

        val adapter = QuestionItemRvAdapter{ questionItem->
            val intent = Intent(requireContext(), QuestionDetails::class.java)
            intent.putExtra("question_item", questionItem)
            startActivity(intent)
        }

        // making the network calls with coroutines
        lifecycleScope.launch {
            Log.d(TAG, "onViewCreated: calling getQuestionCategories")
            viewModel.getQuestionCategories()

            Log.d(TAG, "onViewCreated: calling getAllQuestionsWithoutPaging")
            viewModel.getQuestionsWithoutPaging(null)
        }

        viewModel.questionCategoriesLiveData.observe(requireActivity(), Observer {
            val listOfQuestionCategories: List<QuestionCategoryItem> = it
            binding?.let {

                binding?.chipGroupQuestionCategoriesChips?.removeAllViews()

                context?.let {
                    var chip: Chip
                    val chipGroup = binding?.chipGroupQuestionCategoriesChips

                    chip=Chip(context)
                    chip.text = "All"
                    chip.isCheckable = true
                    chipGroup?.addView(chip)
                    val allQuestionChipId: Int = chip.id

                    for (questionCategory in listOfQuestionCategories) {
                        chip = Chip(context)
                        chip.text = questionCategory.categoryTitle
                        chip.isCheckable = true
                        chipGroup?.addView(chip)
                    }
                    binding?.chipGroupQuestionCategoriesChips?.check(allQuestionChipId)
                }

            }
        })

        binding?.chipGroupQuestionCategoriesChips?.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            var modifiedQuestionList: List<QuestionItem>? = null

            if(chip.text.equals("All")){
                modifiedQuestionList=questions
            }
            else{
                 modifiedQuestionList= questions?.filter { questionItem ->
                     questionItem.question_sub_text == chip.text
                 }
            }
            adapter.submitList(null)
            if (modifiedQuestionList!=null && modifiedQuestionList.isEmpty()) {
                Snackbar.make(
                    binding!!.rvQuestionsList,
                    "Oops! No Questions. Be the first one to ask.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            adapter.submitList(modifiedQuestionList)
            binding?.rvQuestionsList?.smoothScrollToPosition(0)

        }

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


        with(_binding?.rvQuestionsList){
            this?.layoutManager = LinearLayoutManager(requireContext())

            this?.adapter = adapter
        }

        //questions data begin
        viewModel.questionsWithoutPaging.observe(viewLifecycleOwner, Observer {
            questions = it // update the question list
            (binding?.rvQuestionsList?.adapter as QuestionItemRvAdapter).submitList(questions)
            // if empty fetch from network
            if(it.isEmpty()){
                Log.d(TAG, "onViewCreated: rv empty")
                viewModel.getQuestionsWithoutPaging(null)
            }
        })
        //question data end


        // handle the FAB to open the AskQuestionActivity
        binding?.fabAskQuestion?.setOnClickListener {
            val intent = Intent(requireActivity(), AskQuestionActivity::class.java)
            startActivity(intent)
        }
    }

    // set the view binding object to null upon destroying the view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

    override fun onItemClick(questionItem: QuestionItem) {
        val intent = Intent(requireContext(), QuestionDetails::class.java)
        intent.putExtra("question_item", questionItem)
        startActivity(intent)
    }
}