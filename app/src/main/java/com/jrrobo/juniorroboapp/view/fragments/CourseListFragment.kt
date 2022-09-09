package com.jrrobo.juniorroboapp.view.fragments

import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jrrobo.juniorroboapp.databinding.FragmentCourseListBinding
import com.jrrobo.juniorroboapp.view.adapter.CourseListItemAdapter
import com.jrrobo.juniorroboapp.viewmodel.FragmentLiveClassesViewModel
import kotlinx.coroutines.launch

class CourseListFragment : Fragment() {

    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentCourseListBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentCourseListBinding?
        get() = _binding

    // view model for this fragment
    private val viewModel: FragmentLiveClassesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCourseListBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.courseListBackBtn?.setOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = CourseListItemAdapter {
            findNavController().navigate(CourseListFragmentDirections.actionCourseListFragmentToCourseGradeFragment(it))
        }

        with(binding?.rvCourseCategoriesList){
            this?.layoutManager = LinearLayoutManager(requireContext())
            this?.adapter = adapter
        }

        viewModel.getCourseCategories()

        lifecycleScope.launch {
            viewModel.courseListGetFlow.collect {
                when (it) {
                    is FragmentLiveClassesViewModel.CourseListGetEvent.Loading -> {

                    }

                    is FragmentLiveClassesViewModel.CourseListGetEvent.Failure -> {

                    }

                    is FragmentLiveClassesViewModel.CourseListGetEvent.Success -> {
                        if(binding?.rvCourseCategoriesList?.adapter!=null){
                            (binding?.rvCourseCategoriesList?.adapter as CourseListItemAdapter).submitList(it.courseList)
                        }

                    }
                    else -> {
                        Unit
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}