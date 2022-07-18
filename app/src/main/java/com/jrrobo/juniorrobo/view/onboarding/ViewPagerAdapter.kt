package com.jrrobo.juniorrobo.view.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    list: ArrayList<Fragment>,
    fragmentManager: FragmentManager,
    lifeCycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifeCycle) {

    // array list of fragments screens for on-boarding
    private val fragmentList = list

    // returning the fragment screens count
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    // return the fragment of specified position
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}