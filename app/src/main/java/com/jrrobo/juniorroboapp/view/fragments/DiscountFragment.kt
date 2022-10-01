package com.jrrobo.juniorroboapp.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.jrrobo.juniorroboapp.data.booking.BookingItem
import com.jrrobo.juniorroboapp.data.booking.BookingItemPostResponse
import com.jrrobo.juniorroboapp.data.transaction.PayUResponse
import com.jrrobo.juniorroboapp.data.course.CourseGradeDetail
import com.jrrobo.juniorroboapp.data.profile.StudentProfileData
import com.jrrobo.juniorroboapp.data.voucher.Voucher
import com.jrrobo.juniorroboapp.databinding.FragmentDiscountBinding
import com.jrrobo.juniorroboapp.network.PayUConstants
import com.jrrobo.juniorroboapp.utility.setSafeOnClickListener
import com.jrrobo.juniorroboapp.viewmodel.FragmentLiveClassesViewModel
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import kotlinx.coroutines.*


class DiscountFragment : Fragment() {
    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentDiscountBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentDiscountBinding
        get() = _binding!!

    // view model for this fragment
    private val viewModel: FragmentLiveClassesViewModel by activityViewModels()

    private lateinit var courseGradeDetail: CourseGradeDetail

    // to get the voucher details if any
    private var voucher: Voucher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // getting the courseListItem sent from CourseListFragment
        courseGradeDetail = DiscountFragmentArgs.fromBundle(requireArguments()).courseGradeDetail
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ")
        _binding = FragmentDiscountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        updateViews(courseGradeDetail.fee, 0, 0, 0)

        binding.backImageButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonPayMoney.setSafeOnClickListener {
            startTransaction()
            Log.d(TAG, "payMoney set On Click Listener: inside")
        }

        binding.editTextVoucherId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.textViewCouponApplied.visibility = View.GONE
                }
            }
        })


        binding.buttonApplyCoupon.setOnClickListener {
            if (binding.editTextVoucherId.text.isNullOrEmpty()) {
                Snackbar.make(binding.root, "Please apply a coupon first", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.getDiscount(binding.editTextVoucherId.text.toString())
                viewLifecycleOwner.lifecycleScope.launch {
                    // repeatOnLifecycle launches the block in a new coroutine every time the
                    // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.discountGetFlow.collect {
                            when (it) {
                                is FragmentLiveClassesViewModel.DiscountGetEvent.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                    binding.root.isEnabled = false
                                }

                                is FragmentLiveClassesViewModel.DiscountGetEvent.Failure -> {
                                    binding.progressBar.visibility = View.GONE
                                    binding.root.isEnabled = true
                                    Toast.makeText(
                                        requireContext(),
                                        "Coupon Invalid!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    updateViews(courseGradeDetail.fee, 0, 0, 0)
                                    binding.textViewCouponApplied.visibility = View.GONE
                                }

                                is FragmentLiveClassesViewModel.DiscountGetEvent.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    binding.root.isEnabled = true
                                    if (it.voucher.amount.toInt() >= courseGradeDetail.fee) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Coupon cannot be applied to this course!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.textViewCouponApplied.visibility = View.GONE
                                        updateViews(courseGradeDetail.fee, 0, 0, 0)
                                    } else {
                                        voucher =
                                            it.voucher // updating the voucher variable to store the voucher details
                                        binding.textViewCouponApplied.visibility = View.VISIBLE
                                        updateViews(
                                            courseGradeDetail.fee,
                                            0,
                                            it.voucher.amount.toInt(),
                                            0
                                        )
                                    }

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

    }

    private fun startTransaction() {
//        viewModel.getPkStudentIdPreference().observe(viewLifecycleOwner, Observer {
//            binding.progressBar.visibility = View.VISIBLE
//            pkStudentId = it
//            Log.d(TAG, "startTransaction: pkStudentId")
//            getProfileDetails(pkStudentId)
//        })
        viewLifecycleOwner.lifecycleScope.launch {
            val pkStudentId = viewLifecycleOwner.lifecycleScope.async {
                viewModel.getPkStudentIdPreference()
            }
            val response = pkStudentId.await()
            if(response == -1){
                Toast.makeText(requireContext(),"Invalid User Id, Please update your profile!",Toast.LENGTH_SHORT).show()
            }
            else{
                getProfileDetails(response)
            }
        }

    }

    private fun getProfileDetails(pkStudentId: Int) {
        Log.d(TAG, "getProfileDetails: inside------")
        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            val studentProfileData = viewLifecycleOwner.lifecycleScope.async {
                viewModel.getStudentProfile(pkStudentId)
            }

            when (studentProfileData.await()) {
                is FragmentLiveClassesViewModel.ProfileGetEvent.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is FragmentLiveClassesViewModel.ProfileGetEvent.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Couldn't get profile details",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // upon successful GET event populate the profile data
                is FragmentLiveClassesViewModel.ProfileGetEvent.Success -> {
                    // assign the data to all the edit texts
                    val studentData =
                        (studentProfileData.await() as FragmentLiveClassesViewModel.ProfileGetEvent.Success).parsedStudentProfileData
                    if(studentData.firstName.isEmpty() || studentData.mobile.isEmpty()
                        || studentData.email.isEmpty()){
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                        "Invalid user details. Please update your profile!",
                            Toast.LENGTH_SHORT
                            ).show()
                    }
                    else{
                        postBookingItem(studentData)
                    }
                    Log.d(TAG, "Profile Get Event:Success ")
                }
                else -> {
                }
            }
            Log.d(TAG, "postBookingItem: new function success")
        }

    }

    private fun postBookingItem(studentProfileData: StudentProfileData) {
        viewLifecycleOwner.lifecycleScope.launch {
            val bookingItemPostResponse = viewLifecycleOwner.lifecycleScope.async {
                viewModel.postBookingItem(
                    BookingItem(
                        0,
                        courseGradeDetail.fee.toString(),
                        voucher?.amount ?: "0",
                        courseGradeDetail.id,
                        studentProfileData.pkStudentId,
                        voucher?.id ?: 0,
                        null,
                        "Pending"
                    )
                )
            }

            when (bookingItemPostResponse.await()) {
                is FragmentLiveClassesViewModel.BookingItemPostEvent.Loading -> {
                }

                is FragmentLiveClassesViewModel.BookingItemPostEvent.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Couldn't post booking item",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // upon successful GET event populate the profile data
                is FragmentLiveClassesViewModel.BookingItemPostEvent.Success -> {
                    // assign the data to all the edit texts
                    val bookingData =
                        (bookingItemPostResponse.await() as FragmentLiveClassesViewModel.BookingItemPostEvent.Success).bookingItemPostResponse
                    initiatePayUTransaction(bookingData, studentProfileData)
                    Log.d(TAG, "Booking Post Event:Success ")
                }
                else -> {
                }
            }
        }
    }

    private fun initiatePayUTransaction(
        bookingItemPostResponse: BookingItemPostResponse,
        studentProfileData: StudentProfileData
    ) {
        if (courseGradeDetail.fee == 0) {
            Snackbar.make(binding.root, "Invalid Course Amount", Snackbar.LENGTH_SHORT).show()
        } else {

            val payUPaymentParams = PayUPaymentParams.Builder()
                .setAmount("1.0")
                .setIsProduction(true)
                .setKey(PayUConstants.MERCHANT_KEY)
                .setProductInfo(courseGradeDetail.title)
                .setPhone(studentProfileData.mobile)
                .setTransactionId(bookingItemPostResponse.txnid)
                .setFirstName(studentProfileData.firstName)
                .setEmail(studentProfileData.email)
                .setSurl(PayUConstants.Surl)
                .setFurl(PayUConstants.Furl)
                .build()

            Log.d(TAG, "initiatePayUTransaction: inside----")

            PayUCheckoutPro.open(
                requireActivity(), payUPaymentParams,
                object : PayUCheckoutProListener {

                    override fun onPaymentSuccess(response: Any) {

                        response as HashMap<*, *>
                        val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                        val merchantResponse =
                            response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                        Log.d(TAG, "onPaymentSuccess[payUResponse]: ${payUResponse}")
                        Log.d(TAG, "onPaymentSuccess[merchantResponse]: ${merchantResponse}")
                        binding.progressBar.visibility = View.GONE

                        val payUResponseClass : PayUResponse = Gson().fromJson(payUResponse.toString(),
                            PayUResponse::class.java)
                        Log.d(TAG, "onPaymentSuccess: ${payUResponseClass}")
                        Log.d(TAG, "onPaymentSuccess: ${payUResponseClass.status}")
                        Toast.makeText(requireContext(),"Payment Successful!",Toast.LENGTH_SHORT).show()
                    }


                    override fun onPaymentFailure(response: Any) {
                        response as HashMap<*, *>
                        val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                        val merchantResponse =
                            response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                        Log.d(TAG, "onPaymentSuccess[payUResponse]: ${payUResponse}")

                        Log.d(TAG, "onPaymentSuccess[merchantResponse]: ${merchantResponse}")
                        binding.progressBar.visibility = View.GONE
                        val payUResponseClass : PayUResponse = Gson().fromJson(payUResponse.toString(),
                            PayUResponse::class.java)
                        Log.d(TAG, "onPaymentSuccess: ${payUResponseClass}")
                        Log.d(TAG, "onPaymentSuccess: ${payUResponseClass.status}")
                        Toast.makeText(requireContext(),"Payment Failed!",Toast.LENGTH_SHORT).show()
                    }


                    override fun onPaymentCancel(isTxnInitiated: Boolean) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),"Payment Canceled!",Toast.LENGTH_SHORT).show()

                    }


                    override fun onError(errorResponse: ErrorResponse) {
                        binding.progressBar.visibility = View.GONE
                        val errorMessage: String
                        if (errorResponse != null && errorResponse.errorMessage != null && errorResponse.errorMessage!!.isNotEmpty())
                            errorMessage = errorResponse.errorMessage!!
                        else
                            errorMessage = "Some error occurred"

                        Log.d(TAG, "onError: Error occurred in transactions, ${errorMessage}")
                    }

                    override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                        //For setting webview properties, if any. Check Customized Integration section for more details on this
                    }

                    override fun generateHash(
                        valueMap: HashMap<String, String?>,
                        hashGenerationListener: PayUHashGenerationListener
                    ) {
                        if (valueMap.containsKey(CP_HASH_STRING)
                            && valueMap.containsKey(CP_HASH_STRING) != null
                            && valueMap.containsKey(CP_HASH_NAME)
                            && valueMap.containsKey(CP_HASH_NAME) != null
                        ) {

                            val hashData = valueMap[CP_HASH_STRING]
                            val hashName = valueMap[CP_HASH_NAME]

                            //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                            if (hashData != null) {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    val hash = viewLifecycleOwner.lifecycleScope.async {
                                        viewModel.getHash(hashData)
                                    }

                                    when (hash.await()) {
                                        is FragmentLiveClassesViewModel.HashGetEvent.Loading -> {
                                        }

                                        is FragmentLiveClassesViewModel.HashGetEvent.Failure -> {
                                            Toast.makeText(
                                                requireContext(),
                                                "Couldn't get hash from server",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        // upon successful GET event populate the profile data
                                        is FragmentLiveClassesViewModel.HashGetEvent.Success -> {
                                            // assign the data to all the edit texts
                                            val hashString =
                                                (hash.await() as FragmentLiveClassesViewModel.HashGetEvent.Success).sha512
                                            if (!TextUtils.isEmpty(hashString)) {
                                                val dataMap: HashMap<String, String?> = HashMap()
                                                dataMap[hashName!!] = hashString
                                                hashGenerationListener.onHashGenerated(dataMap) // call
                                            }
                                            Log.d(TAG, "Hash Get Event:Success ")
                                        }
                                        else -> {
                                        }
                                    }
                                }
                            }

                        }
                    }
                })
        }

    }

    private fun updateViews(
        totalMRP: Int,
        discountOnMRP: Int,
        couponDiscount: Int,
        convenienceFee: Int
    ) {
        val totalAmount = totalMRP + convenienceFee - (discountOnMRP + couponDiscount)
        binding.totalMrpTextView.text = totalMRP.toString()
        binding.discountOnMrpTextView.text = discountOnMRP.toString()
        binding.discountTextView.text = couponDiscount.toString()
        binding.convenienceFeeTextView.text = convenienceFee.toString()
        binding.totalAmountTextView.text = totalAmount.toString()
    }

    // set the view binding object to null upon destroying the view
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
        _binding = null
    }
}