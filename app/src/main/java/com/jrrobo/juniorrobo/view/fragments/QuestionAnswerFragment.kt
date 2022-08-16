package com.jrrobo.juniorrobo.view.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorrobo.databinding.FragmentQuestionAnswerBinding
import com.jrrobo.juniorrobo.databinding.PopUpLayoutBinding
import com.jrrobo.juniorrobo.view.activities.AskQuestionActivity
import com.jrrobo.juniorrobo.view.activities.QuestionDetails
import com.jrrobo.juniorrobo.view.adapter.QuestionItemRvAdapter
import com.jrrobo.juniorrobo.viewmodel.FragmentQuestionsViewModel
import kotlinx.coroutines.launch


class QuestionAnswerFragment : Fragment() {

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

        showPopUpDialog()


        // primary key of the student initialised to -1
        var pkStudentId: Int = -1

        // launch coroutine for requesting the primary key and displaying the profile data

        // request the primary key
        viewModel.getPkStudentIdPreference().observe(requireActivity(), Observer {
            // assign the primary key from the data store preference
            pkStudentId = it
        })

        val catNameToCatIdMap: HashMap<String,Int> = HashMap()

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
//            viewModel.getQuestionsWithoutPaging(null)
        }

        var listOfQuestionCategories: List<QuestionCategoryItem>? = null
        viewModel.questionCategoriesLiveData.observe(requireActivity(), Observer {
            listOfQuestionCategories = it
            binding?.let {
                binding?.chipGroupQuestionCategoriesChips?.removeAllViews()

                context?.let {
                    var chip: Chip
                    val chipGroup = binding?.chipGroupQuestionCategoriesChips

                    // All question category
                    chip=Chip(context)
                    chip.text = "All"
                    chip.isCheckable = true
                    chipGroup?.addView(chip)

                    val allQuestionChipId: Int = chip.id

                    // My questions category
                    chip=Chip(context)
                    chip.text = "My questions"
                    chip.isCheckable = true
                    chipGroup?.addView(chip)

                    listOfQuestionCategories?.let {
                        for (questionCategory in listOfQuestionCategories!!) {
                            catNameToCatIdMap[questionCategory.categoryTitle] = questionCategory.pkCategoryId
                            chip = Chip(context)
                            chip.text = questionCategory.categoryTitle
                            chip.isCheckable = true
                            chipGroup?.addView(chip)
                        }
                    }
                    binding?.chipGroupQuestionCategoriesChips?.check(allQuestionChipId)
                }

            }
        })

        binding?.chipGroupQuestionCategoriesChips?.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            if(chip.text.equals("All")){
                lifecycleScope.launch {
                    viewModel.getQuestionsWithoutPaging(null,null,null)
                }
            }
            else if(chip.text.equals("My questions")){
                lifecycleScope.launch {
                    viewModel.getQuestionsWithoutPaging(null,null,pkStudentId)
                }
            }
            else{
                lifecycleScope.launch{
                    viewModel.getQuestionsWithoutPaging(listOfQuestionCategories?.find { questionCategoryItem ->
                        questionCategoryItem.categoryTitle == chip.text
                    }?.pkCategoryId,null,null)
                }
            }
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
            (binding?.rvQuestionsList?.adapter as QuestionItemRvAdapter).submitList(it)
            binding?.rvQuestionsList?.smoothScrollToPosition(0)
            if(it.isEmpty()){
                val toast = Toast.makeText(requireContext(), "Oops! No Questions. Be the first one to ask.",
                    Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }


        })
        //question data end


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
                    lifecycleScope.launch {
                        val chipGroup = binding?.chipGroupQuestionCategoriesChips
                        val chip = chipGroup!!.findViewById<Chip>(chipGroup.checkedChipId)
                        val categoryId = catNameToCatIdMap[chip.text]
                        if(chip.text == "My questions"){
                            viewModel.getQuestionsWithoutPaging(categoryId,query?.trim().toString(),pkStudentId)
                        }
                        else{
                            viewModel.getQuestionsWithoutPaging(categoryId,query?.trim().toString(),null)
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun showPopUpDialog() {

        lifecycleScope.launch {
            val dialogBinding = layoutInflater.inflate(R.layout.pop_up_layout,null)

            val dialog = Dialog(requireContext())
            dialog.setContentView(dialogBinding)
            dialog.setCancelable(true)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val  cancelButton = dialogBinding.findViewById<TextView>(R.id.text_view_cancel)
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
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
}