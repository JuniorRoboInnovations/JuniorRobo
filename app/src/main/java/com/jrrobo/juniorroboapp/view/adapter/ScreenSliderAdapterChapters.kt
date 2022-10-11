package com.jrrobo.juniorroboapp.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jrrobo.juniorroboapp.view.fragments.FragmentClassroomLiveChapters
import com.jrrobo.juniorroboapp.view.fragments.FragmentClassroomRecordedChapters

class ScreenSliderAdapterChapters (fa : FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentClassroomLiveChapters()
            else -> FragmentClassroomRecordedChapters()
        }
    }
}