package com.jrrobo.juniorroboapp.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.databinding.ActivityCourseDetailBinding
import com.jrrobo.juniorroboapp.utility.ScreenSliderAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseDetailActivity : AppCompatActivity() {

    // TAG for logging purpose
    private val TAG: String = javaClass.simpleName

    // view binding object
    private lateinit var binding: ActivityCourseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val courseGradeListItem = intent.extras?.getParcelable<CourseGradeListItem>("courseGradeListItem")

        binding.toolBarTitle.title = courseGradeListItem?.title
        supportActionBar?.title = binding.toolBarTitle.title

        binding.viewPager.adapter = ScreenSliderAdapter(this, courseGradeListItem!!)
        TabLayoutMediator(binding.tabs,binding.viewPager) {
                tab, position ->
            when(position){
                0 -> tab.text = "BATCH"
                1 -> tab.text = "1:1"
            }
        }.attach()
    }
}