package com.jrrobo.juniorrobo.view.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jrrobo.juniorrobo.databinding.FragmentOtpVerificationBinding
import com.jrrobo.juniorrobo.utility.Utilities
import com.jrrobo.juniorrobo.viewmodel.FragmentLoginViewModel

class OtpVerificationFragment : Fragment() {

    // view binding object
    private var _binding: FragmentOtpVerificationBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentOtpVerificationBinding
        get() = _binding!!

    private val viewModel: FragmentLoginViewModel by activityViewModels()

    private val TAG: String = javaClass.simpleName

    private val navigationArgs: OtpVerificationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)

        // set up text change listener for all the OTP digit text fields
        setupOtpTextFields()

        // get the contact number entered previously by the user (from the LoginFragment)
        binding.textViewMobileNumber.text = navigationArgs.contactNumber

        // as soon as the fragment is created then hide the keyboard
        Utilities.hideKeyboard(requireActivity())

        // store the received contact number from the navigation args
        val contactNumber: String =
            navigationArgs.contactNumber.split(" ")[0] + navigationArgs.contactNumber.split(" ")[1]
        // Log the contact number received
        Log.d(TAG, contactNumber)

        // when the verify otp button is clicked handle the listener for requesting OTP verification
        binding.buttonVerifyOtp.setOnClickListener {

            // get the Digits from each of the digit edit texts
            val userOtp =
                "${binding.otpEditBox1.text}${binding.otpEditBox2.text}${binding.otpEditBox3.text}${binding.otpEditBox4.text}"

            // get the response from server for verification of the OTP
            viewModel.responseOtp(contactNumber, userOtp)

            // after entering all the four digits of the OTP hide the keyboard
            Utilities.hideKeyboard(requireActivity())
        }

        // create the instance of the alert dialog and alertdialog builder and assign to null
        var alertDialog: AlertDialog? = null
        var alertDialogBuilder: AlertDialog.Builder?

        // launch coroutine for getting the 4 OTP verification response
        lifecycleScope.launchWhenStarted {
            viewModel.otpResponseFlow.collect { otpResponseEvent ->
                when (otpResponseEvent) {

                    // collect the event of the Loading state of the OTP verification response
                    is FragmentLoginViewModel.OtpEvent.Loading -> {

                        // if any alert dialogs are there then dismiss them
                        alertDialog!!.dismiss()

                        // create the alert dialog for loading purpose
                        alertDialogBuilder =
                            AlertDialog.Builder(requireActivity())
                        alertDialogBuilder!!.setMessage("Please wait")
                        alertDialogBuilder!!.setCancelable(false)
                        alertDialog = alertDialogBuilder!!.create()
                        alertDialog!!.show()
                    }

                    // collect the Success event of the OTP verification response
                    is FragmentLoginViewModel.OtpEvent.Success -> {
                        Log.d(TAG, otpResponseEvent.resultText)

                        // dismiss previous dialogs
                        alertDialog!!.dismiss()

                        // build the alert dialog for redirecting the user to home page
                        alertDialogBuilder =
                            AlertDialog.Builder(requireActivity())
                        alertDialogBuilder!!.setMessage(otpResponseEvent.resultText)
                        alertDialogBuilder!!.setCancelable(false)
                        alertDialogBuilder!!.setNegativeButton(
                            "GO TO HOME",
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    alertDialog!!.dismiss()

                                    val navigationDirections =
                                        OtpVerificationFragmentDirections.actionOtpVerificationFragmentToFromQuestionAnswerActivity(
                                            contactNumber
                                        )
                                    findNavController().navigate(
                                        navigationDirections
                                    )
                                }
                            })

                        alertDialog = alertDialogBuilder!!.create()
                        alertDialog!!.show()

                        viewModel.setOtpVerificationStatus(true)
                    }

                    // collect the otp verification response event for any failure occurred
                    is FragmentLoginViewModel.OtpEvent.Failure -> {
                        Log.d(TAG, otpResponseEvent.errorText)

                        // dismiss the previous alert dialogs
                        alertDialog!!.dismiss()

                        // build the alert dialog for showing any failure messages
                        alertDialogBuilder =
                            AlertDialog.Builder(requireActivity())
                        alertDialogBuilder!!.setMessage(otpResponseEvent.errorText)
                        alertDialogBuilder!!.setCancelable(false)
                        alertDialogBuilder!!.setNegativeButton(
                            "CLOSE",
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    alertDialog!!.dismiss()
                                }
                            })

                        alertDialog = alertDialogBuilder!!.create()
                        alertDialog!!.show()
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }

        // Now launch the coroutine for requesting the OTP for entered number
        lifecycleScope.launchWhenStarted {

            // collect the flow for OTP request for entered number
            viewModel.otpFlow.collect { otpRequestEvent ->
                when (otpRequestEvent) {

                    // collect the Loading event for the OTP request event
                    is FragmentLoginViewModel.OtpEvent.Loading -> {

                        // build the alert dialog for showing the loading status of the OTP request
                        alertDialogBuilder =
                            AlertDialog.Builder(requireActivity())
                        alertDialogBuilder!!.setMessage("Please wait")
                        alertDialogBuilder!!.setCancelable(false)
                        alertDialog = alertDialogBuilder!!.create()
                        alertDialog!!.show()
                    }

                    // collect the success event of the OTP request
                    is FragmentLoginViewModel.OtpEvent.Success -> {

                        // dismiss the previous alert dialog
                        alertDialog!!.dismiss()

                        // build the alert dialog for showing the close button
                        alertDialogBuilder =
                            AlertDialog.Builder(requireActivity())
                        Log.d(TAG, "onCreateView:Succes-> ${otpRequestEvent.resultText}")
                        alertDialogBuilder!!.setMessage(otpRequestEvent.resultText)
                        alertDialogBuilder!!.setCancelable(false)
                        alertDialogBuilder!!.setNegativeButton(
                            "CLOSE",
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    alertDialog!!.dismiss()
                                }
                            })

                        alertDialog = alertDialogBuilder!!.create()
                        alertDialog!!.show()
                    }

                    // collect the failure event of the OTP request event
                    is FragmentLoginViewModel.OtpEvent.Failure -> {

                        // dismiss the previous alert dialogs
                        alertDialog!!.dismiss()

                        // build the alert dialog to show the error message
                        alertDialogBuilder =
                            AlertDialog.Builder(requireActivity())
                        Log.d(TAG, "onCreateView:Failure ${otpRequestEvent.errorText}")
                        alertDialogBuilder!!.setMessage(otpRequestEvent.errorText)
                        alertDialogBuilder!!.setCancelable(false)
                        alertDialogBuilder!!.setNegativeButton(
                            "CLOSE",
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    alertDialog!!.dismiss()
                                }
                            })

                        alertDialog = alertDialogBuilder!!.create()
                        alertDialog!!.show()
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }

        return binding.root
    }

    /**
     *  function which initialises with auto focus feature for each of the edit text boxes for OTP digits
     */
    private fun setupOtpTextFields() {
        binding.otpEditBox1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) {
                    binding.otpEditBox2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.otpEditBox2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) {
                    binding.otpEditBox3.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    binding.otpEditBox1.requestFocus()
                }
            }

        })

        binding.otpEditBox3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) {
                    binding.otpEditBox4.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    binding.otpEditBox2.requestFocus()
                }
            }

        })

        binding.otpEditBox4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    binding.otpEditBox3.requestFocus()
                }
            }

        })
    }

    // set the view binding object to null upon destroying the view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}