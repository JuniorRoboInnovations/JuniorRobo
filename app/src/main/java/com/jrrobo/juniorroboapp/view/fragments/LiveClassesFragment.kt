package com.jrrobo.juniorroboapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.databinding.FragmentLiveClassesBinding

class LiveClassesFragment : Fragment() {

    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentLiveClassesBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentLiveClassesBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inflating layout using binding object
        _binding = FragmentLiveClassesBinding.inflate(inflater, container, false)

//        Log.d(TAG, requireContext().packageName)

//        val introVideoPath =
//            "android.resource://" + requireContext().packageName + "/" + R.raw.live_class_fragment_intro
//        val videoUri = Uri.parse(introVideoPath)
//        binding.videoViewLiveClassIntro.setVideoURI(videoUri)
//
//        binding.videoViewLiveClassIntro.requestFocus()
//
//        val mediaController = MediaController(requireContext())
//        binding.videoViewLiveClassIntro.setMediaController(mediaController)
//        mediaController.setAnchorView(binding.videoViewLiveClassIntro)
//
//        binding.videoViewLiveClassIntro.setOnPreparedListener { binding.videoViewLiveClassIntro.start() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentLiveClassesStartLearningBtn.setOnClickListener {
            findNavController().navigate(R.id.action_liveClassesFragment_to_courseListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}