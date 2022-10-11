package com.jrrobo.juniorroboapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.jrrobo.juniorroboapp.databinding.FragmentClassroomSubjectsBinding
import com.jrrobo.juniorroboapp.view.adapter.SubjectsRvAdapter
import kotlinx.coroutines.launch


class FragmentClassroomSubjects : Fragment() {

    // view binding object
    private var _binding: FragmentClassroomSubjectsBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentClassroomSubjectsBinding
        get() = _binding!!

    private val listOfCourses = listOf(
        "CBSE G10",
        "CBSE G09",
        "CBSE G08",
        "CBSE G07",
        "CBSE G06",
        "CBSE G05",
        "CBSE G04"
    )

    private val listOfSubjects = listOf(
        "Hindi",
        "English",
        "Maths",
        "Science",
        "Physics",
        "Chemistry"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClassroomSubjectsBinding.inflate(inflater, container, false)

        val catNameToCatIdMap: HashMap<String, Int> = HashMap()

        binding?.let {
            binding?.chipGroupCoursesChips?.removeAllViews()

            context?.let {
                var chip: Chip
                val chipGroup = binding?.chipGroupCoursesChips

                // All question category
                chip = Chip(context)
                chip.text = "All"
                chip.isCheckable = true
                chipGroup?.addView(chip)

                val allQuestionChipId: Int = chip.id

                listOfCourses?.let {
                    for (questionCategory in listOfCourses!!) {
                        catNameToCatIdMap[questionCategory] = questionCategory.length
                        chip = Chip(context)
                        chip.text = questionCategory
                        chip.isCheckable = true
                        chipGroup?.addView(chip)
                    }
                }
                binding?.chipGroupCoursesChips?.check(allQuestionChipId)
            }
            binding?.chipGroupCoursesChips?.setOnCheckedChangeListener { group, checkedId ->
                val chip = group.findViewById<Chip>(checkedId)
                if (chip.text.equals("All")) {
                    /*lifecycleScope.launch {
                viewModel.getQuestionsWithoutPaging(null,null,null)
            }*/
                } else {
                    lifecycleScope.launch {
                        listOfCourses?.find { questionCategoryItem ->
                            questionCategoryItem == chip.text
                        }
                    }
                }
            }
        }

        binding.subjectsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SubjectsRvAdapter(listOfSubjects)
        }
            return binding.root
    }
}