package com.jrrobo.juniorroboapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jrrobo.juniorroboapp.data.classroom.ClassroomChapters
import com.jrrobo.juniorroboapp.databinding.FragmentClassroomLiveChaptersBinding
import com.jrrobo.juniorroboapp.view.adapter.LiveChaptersRvAdapter
import com.jrrobo.juniorroboapp.viewmodel.FragmentClassroomViewModel


class FragmentClassroomLiveChapters : Fragment() {

    // view binding object
    private var _binding: FragmentClassroomLiveChaptersBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentClassroomLiveChaptersBinding
        get() = _binding!!

    private val listOfChapters = listOf(
        "chapter 1",
        "chapter 2",
        "chapter 3",
        "chapter 4",
        "chapter 5",
        "chapter 6",
        "chapter 7",
        "chapter 8",
        "chapter 9",
    )

    private lateinit var list: List<ClassroomChapters>

    private val viewModel: FragmentClassroomViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClassroomLiveChaptersBinding.inflate(inflater, container, false)
        binding.chaptersRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = LiveChaptersRvAdapter(listOfChapters)
        }
        return binding.root
    }

}