package com.jrrobo.juniorrobo.view.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jrrobo.juniorrobo.databinding.FragmentLoginBinding
import com.jrrobo.juniorrobo.viewmodel.FragmentLoginViewModel

class LoginFragment : Fragment() {

    // view binding object
    private var _binding: FragmentLoginBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private val TAG = javaClass.simpleName

    private val viewModel: FragmentLoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // inflating layout using binding object
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // create the clickable link for the terms and condition and privacy policy as well
        binding.textviewTerms.movementMethod = LinkMovementMethod.getInstance()

        // the request OTP button should be shown only when user agrees terms checkbox
        binding.checkboxTerms.setOnCheckedChangeListener { _, isChecked ->
            binding.buttonRequestOtp.isEnabled = isChecked
        }

        // handle the request otp button click for requesting the OTP
        binding.buttonRequestOtp.setOnClickListener {

            val countryCode: String =
                binding.countryCodePicker.selectedCountryCode.trim()
            val contactNumber: String = binding.editTextPhoneNumber.text.toString()

            // request the OTP after extracting the country code and the contact number
            viewModel.requestOtp(countryCode + contactNumber)

            // navigate to the OtpVerificationFragment by passing the country code and contact number
            val navigationAction =
                LoginFragmentDirections.actionLoginFragmentToOtpVerificationFragment(
                    "$countryCode $contactNumber"
                )
            findNavController().navigate(navigationAction)
        }

        return binding.root
    }

    // set the view binding object to null upon destroying the view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}