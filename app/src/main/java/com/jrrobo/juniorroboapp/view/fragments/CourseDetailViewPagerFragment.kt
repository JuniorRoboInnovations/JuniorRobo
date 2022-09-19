package com.jrrobo.juniorroboapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.databinding.FragmentCourseDetailViewPagerBinding
import com.jrrobo.juniorroboapp.databinding.FragmentViewPagerBinding
import com.jrrobo.juniorroboapp.utility.ScreenSliderAdapter


class CourseDetailViewPagerFragment : Fragment() {

    private var _binding: FragmentCourseDetailViewPagerBinding? = null

    val binding: FragmentCourseDetailViewPagerBinding
        get() = _binding!!

    private lateinit var courseGradeListItem: CourseGradeListItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseGradeListItem = CourseDetailViewPagerFragmentArgs.fromBundle(requireArguments()).courseGradeListItem
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCourseDetailViewPagerBinding.inflate(inflater, container, false)

        binding.toolBarTitle.text = courseGradeListItem.title
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.viewPager.adapter = ScreenSliderAdapter(requireActivity(), courseGradeListItem)

        TabLayoutMediator(binding.tabs,binding.viewPager) {
                tab, position ->
            when(position){
                0 -> tab.text = "BATCH"
                1 -> tab.text = "1:1"
            }
        }.attach()

        return binding.root
    }
}