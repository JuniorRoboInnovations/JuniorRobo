package com.jrrobo.juniorrobo.view.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.databinding.FragmentSplashScreenBinding
import com.jrrobo.juniorrobo.viewmodel.FragmentLoginViewModel
import com.jrrobo.juniorrobo.viewmodel.FragmentOnBoardLoginViewModel

class SplashFragment : Fragment() {

    // view binding object
    private var _binding: FragmentSplashScreenBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentSplashScreenBinding
        get() = _binding!!

    // view model to track the on-boarding status of the application
    private val fragmentOnBoardLoginViewModel: FragmentOnBoardLoginViewModel by activityViewModels()

    // view model to track the on-boarding status of the application
    private val fragmentLoginViewModel: FragmentLoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // inflating layout using binding object
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)

        // variables to get the on boarding status and otp verification status of the user
        var onBoardingStatusBoolean: Boolean = false
        var otpVerificationStatusBoolean: Boolean = false
        var appLaunchedStatus: Boolean = false

        // get the on boarding status of the user from the data store preferences
        fragmentOnBoardLoginViewModel.getOnBoardStatus().observe(
            viewLifecycleOwner
        ) {
            onBoardingStatusBoolean = it
        }

        // variable to capture when the app is launched
        fragmentLoginViewModel.setAppLaunchedStatus(true)


        // get the otp verification status of the user from the data store preferences
        fragmentLoginViewModel.getOtpVerificationStatus().observe(
            viewLifecycleOwner
        ) {
            otpVerificationStatusBoolean = it
        }

        // Handler for displaying the splash screen for 1 seconds
        Handler(Looper.getMainLooper()).postDelayed({

            if (onBoardingStatusBoolean && otpVerificationStatusBoolean) {
                findNavController().navigate(R.id.action_splashFragment_to_fromQuestionAnswerActivity)
            } else if (onBoardingStatusBoolean && !otpVerificationStatusBoolean) {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }

        }, 1000)

        return binding.root
    }

    // set the view binding object to null upon destroying the view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}