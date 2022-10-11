package com.jrrobo.juniorroboapp.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.view.fragments.CourseDetailFragment
import com.jrrobo.juniorroboapp.view.fragments.FragmentCourseDetailsSecondPage

class ScreenSliderAdapterCourses(fa : FragmentActivity, val courseGradeListItem: CourseGradeListItem) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int  = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CourseDetailFragment(courseGradeListItem)
            else -> FragmentCourseDetailsSecondPage(courseGradeListItem)
        }
    }


}