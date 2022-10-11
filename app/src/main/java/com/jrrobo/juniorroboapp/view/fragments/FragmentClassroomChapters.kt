package com.jrrobo.juniorroboapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.jrrobo.juniorroboapp.databinding.FragmentClassroomChaptersBinding
import com.jrrobo.juniorroboapp.view.adapter.ScreenSliderAdapterChapters

class FragmentClassroomChapters : Fragment() {

    // view binding object
    private var _binding: FragmentClassroomChaptersBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentClassroomChaptersBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClassroomChaptersBinding.inflate(inflater, container, false)

        binding.viewPager.adapter = ScreenSliderAdapterChapters(requireActivity())
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Live"
                1 -> tab.text = "Recorded"
            }
        }.attach()

        return binding.root    }
}