package com.jrrobo.juniorroboapp.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.data.emailLogin.EmailRegisterData
import com.jrrobo.juniorroboapp.databinding.FragmentLoginBinding
import com.jrrobo.juniorroboapp.viewmodel.FragmentLoginViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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

            val dialogBinding = layoutInflater.inflate(R.layout.sign_in_email_layout,null)
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

            val  cancelButton = dialogBinding.findViewById<ImageView>(R.id.image_clear_sign_in)
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            //forgot password text
            val forgotPass = dialogBinding.findViewById<MaterialTextView>(R.id.sign_in_forgot_password)

            val loginButton = dialogBinding.findViewById<MaterialButton>(R.id.sign_in_login_button)
            val passwordEditText = dialogBinding.findViewById<EditText>(R.id.edit_text_password)
            val emailEditText = dialogBinding.findViewById<EditText>(R.id.sign_in_edit_text_email)
            val phoneNumberEditText = dialogBinding.findViewById<EditText>(R.id.edit_text_number)
            val phoneNumberText = dialogBinding.findViewById<TextInputLayout>(R.id.sign_in_number)
            val registerButton = dialogBinding.findViewById<MaterialButton>(R.id.sign_in_register_button)
            val newUserText = dialogBinding.findViewById<MaterialTextView>(R.id.new_user_text)

            newUserText.setOnClickListener {
                registerButton.visibility = View.VISIBLE
                phoneNumberText.visibility = View.VISIBLE
                newUserText.visibility = View.GONE
                loginButton.visibility = View.GONE
                forgotPass.visibility = View.GONE
            }

            registerButton.setOnClickListener {
                if (emailEditText.text.isNullOrEmpty() || passwordEditText.text.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "Please enter a valid Email ID and Password",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val email: String = emailEditText.text.toString()
                    val pass: String = passwordEditText.text.toString()
                    val mobile: String = phoneNumberEditText.text.toString()

                    viewModel.registerEmail(
                        EmailRegisterData(
                            email = email,
                            password = pass,
                            mobile = mobile
                        )
                    )

                    lifecycleScope.launch {

                        viewModel.emailRegisterEventFlow.collect {
                            when (it) {

                                is FragmentLoginViewModel.EmailRegisterEvent.Loading -> {

                                }

                                is FragmentLoginViewModel.EmailRegisterEvent.Failure -> {
                                    Toast.makeText(dialog.context, it.errorText, Toast.LENGTH_SHORT).show()
                                }

                                is FragmentLoginViewModel.EmailRegisterEvent.Success -> {

                                    Log.e(TAG, "showSignInDialog: ${it.emailRegisterPostResponse}", )

                                    Toast.makeText(dialog.context, "Successfully Registered", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                    val navigationDirections =
                                        LoginFragmentDirections.actionLoginFragmentToFromQuestionAnswerActivity()
                                    findNavController().navigate(navigationDirections)
                                }
                                else -> {
                                    Unit
                                }
                            }
                        }
                    }
                }
            }
            loginButton.setOnClickListener {
                if (emailEditText.text.isNullOrEmpty() || passwordEditText.text.isNullOrEmpty()){
                    Toast.makeText(context, "Please enter a valid Email ID and Password", Toast.LENGTH_SHORT).show()
                }
                else{
                    val email:String = emailEditText.text.toString()
                    val pass: String = passwordEditText.text.toString()

                    viewModel.responseEmail(email,pass)

                    lifecycleScope.launch {

                        viewModel.emailResponseFlow.collect {
                            when(it){
                                is FragmentLoginViewModel.EmailEvent.Loading -> {

                                }

                                is FragmentLoginViewModel.EmailEvent.Failure -> {

                                    Log.e(TAG, "onCreateView: ${it.errorText}", )

                                    Toast.makeText(dialog.context, it.errorText, Toast.LENGTH_SHORT).show()
                                }

                                is FragmentLoginViewModel.EmailEvent.Success -> {
                                    Log.e(TAG, "showSignInDialog: ${it.resultText}", )

                                    dialog.dismiss()
                                    val navigationDirections =
                                        LoginFragmentDirections.actionLoginFragmentToFromQuestionAnswerActivity()
                                    findNavController().navigate(navigationDirections)
                                    viewModel.setOtpVerificationStatus(true)
                                }
                                else -> {
                                    Unit
                                }
                            }
                        }
                    }

                }
            }

        }
        return binding.root
    }



        // set the view binding object to null upon destroying the view
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }