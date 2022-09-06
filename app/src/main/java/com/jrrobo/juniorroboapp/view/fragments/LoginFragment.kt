package com.jrrobo.juniorroboapp.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.databinding.FragmentLoginBinding
import com.jrrobo.juniorroboapp.viewmodel.FragmentLoginViewModel

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
    ): View {

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
            if (contactNumber.isEmpty()) {
                Toast.makeText(context, "Please Enter your Phone Number", Toast.LENGTH_SHORT).show()
            } else {
                val navigationAction =
                    LoginFragmentDirections.actionLoginFragmentToOtpVerificationFragment(
                        "$countryCode $contactNumber"
                    )
                findNavController().navigate(navigationAction)
            }
        }

        binding.signInEmail.setOnClickListener {
            Log.e(TAG, "onCreateView: ${R.layout.email_sign_in_layout}", )
            showSignInDialog()
        }

        return binding.root
    }

    private fun showSignInDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.email_sign_in_layout,null)
        val dialog = Dialog(requireContext(),android.R.style.Theme_Translucent_NoTitleBar)

        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.dimAmount = 0.7f

        dialog.window!!.attributes = lp
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        dialog.show()

        val  cancelButton = dialogBinding.findViewById<ImageView>(R.id.image_clear_demo)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    // set the view binding object to null upon destroying the view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}