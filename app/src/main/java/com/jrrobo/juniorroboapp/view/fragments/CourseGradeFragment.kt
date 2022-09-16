package com.jrrobo.juniorroboapp.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.data.course.CourseListItem
import com.jrrobo.juniorroboapp.databinding.FragmentCourseGradeBinding
import com.jrrobo.juniorroboapp.databinding.FragmentCourseListBinding
import com.jrrobo.juniorroboapp.network.EndPoints
import com.jrrobo.juniorroboapp.view.activities.CourseDetailActivity
import com.jrrobo.juniorroboapp.view.activities.QuestionDetails
import com.jrrobo.juniorroboapp.view.adapter.CourseGradeListItemAdapter
import com.jrrobo.juniorroboapp.view.adapter.CourseListItemAdapter
import com.jrrobo.juniorroboapp.viewmodel.FragmentLiveClassesViewModel
import kotlinx.coroutines.launch

class CourseGradeFragment : Fragment() {

    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentCourseGradeBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentCourseGradeBinding
        get() = _binding!!

    // view model for this fragment
    private val viewModel: FragmentLiveClassesViewModel by activityViewModels()

    private lateinit var courseListItem: CourseListItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // getting the courseListItem sent from CourseListFragment
        courseListItem = CourseGradeFragmentArgs.fromBundle(requireArguments()).courseListItem
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCourseGradeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setting up the title of course grade page
        binding.fragmentCourseGradeCourseTitle.text = courseListItem.title

        binding.backImageButton.setOnClickListener {
            findNavController().popBackStack()
        }


        with(binding.rvCourseGradeList){
            this.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            this.adapter = CourseGradeListItemAdapter {
                val intent = Intent(requireContext(), CourseDetailActivity::class.java)
                intent.putExtra("courseGradeListItem", it)
                startActivity(intent)
            }
        }
        viewModel.getCourseGrades(courseListItem.id)

        lifecycleScope.launch {
            viewModel.courseGradeListGetFlow.collect {
                when (it) {
                    is FragmentLiveClassesViewModel.CourseGradeListGetEvent.Loading -> {

                    }

                    is FragmentLiveClassesViewModel.CourseGradeListGetEvent.Failure -> {

                    }

                    is FragmentLiveClassesViewModel.CourseGradeListGetEvent.Success -> {
                        // assign the data to all the edit texts
                        (binding.rvCourseGradeList.adapter as CourseGradeListItemAdapter).submitList(it.courseGradeList)

                        // loading the course image here
                        Glide.with(binding.root)
                            .load(EndPoints.GET_IMAGE + "/course/" + courseListItem.image)
                            .into(binding.courseImage)

                        // loading the description
                        binding.courseGradeAboutText.text = courseListItem.description

                    }
                    else -> {
                        Unit
                    }
                }
            }
        }
    }
}