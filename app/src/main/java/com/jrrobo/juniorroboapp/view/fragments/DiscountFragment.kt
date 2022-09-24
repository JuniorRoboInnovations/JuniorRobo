package com.jrrobo.juniorroboapp.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.data.course.CourseGradeDetail
import com.jrrobo.juniorroboapp.databinding.FragmentDiscountBinding
import com.jrrobo.juniorroboapp.viewmodel.FragmentLiveClassesViewModel
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import kotlinx.coroutines.launch
import java.lang.Integer.max


class DiscountFragment : Fragment() {
    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentDiscountBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentDiscountBinding
        get() = _binding!!

    // view model for this fragment
    private val viewModel: FragmentLiveClassesViewModel by activityViewModels()

    private var coursePrice: Int = 0

    private lateinit var courseGradeDetail: CourseGradeDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // getting the courseListItem sent from CourseListFragment
        coursePrice = DiscountFragmentArgs.fromBundle(requireArguments()).coursePrice
        courseGradeDetail = DiscountFragmentArgs.fromBundle(requireArguments()).courseGradeDetail
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDiscountBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editTextAmount.setText(coursePrice.toString())
        binding.editTextAmount.isEnabled = false

        binding.backImageButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonPayMoney.setOnClickListener{
            if(binding.editTextAmount.text.toString() == ""){
                Snackbar.make(binding.root,"Invalid Course Amount",Snackbar.LENGTH_SHORT).show()
            }
            else{

                val additionalParamsMap: HashMap<String, Any?> = HashMap()
                additionalParamsMap[PayUCheckoutProConstants.CP_UDF1] = "udf1"
                additionalParamsMap[PayUCheckoutProConstants.CP_UDF2] = "udf2"
                additionalParamsMap[PayUCheckoutProConstants.CP_UDF3] = "udf3"
                additionalParamsMap[PayUCheckoutProConstants.CP_UDF4] = "udf4"
                additionalParamsMap[PayUCheckoutProConstants.CP_UDF5] = "udf5"
                additionalParamsMap[PayUCheckoutProConstants.SODEXO_SOURCE_ID] = "srcid123"

                val payUPaymentParams = PayUPaymentParams.Builder()
                    .setAmount("1.0")
                    .setIsProduction(false)
                    .setKey("vvvgTp")
                    .setProductInfo(courseGradeDetail.title)
                    .setPhone("9999999999")
                    .setTransactionId(System.currentTimeMillis().toString())
                    .setFirstName("John")
                    .setEmail("john@yopmail.com")
                    .setSurl("https://payuresponse.firebaseapp.com/success")
                    .setFurl("https://payuresponse.firebaseapp.com/failure")
                    .build()


                PayUCheckoutPro.open(
                    requireActivity(), payUPaymentParams,
                    object : PayUCheckoutProListener {


                        override fun onPaymentSuccess(response: Any) {
                            response as HashMap<*, *>
                            val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                            val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                        }


                        override fun onPaymentFailure(response: Any) {
                            response as HashMap<*, *>
                            val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                            val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                        }


                        override fun onPaymentCancel(isTxnInitiated:Boolean) {
                        }


                        override fun onError(errorResponse: ErrorResponse) {
                            val errorMessage: String
                            if (errorResponse != null && errorResponse.errorMessage != null && errorResponse.errorMessage!!.isNotEmpty())
                                errorMessage = errorResponse.errorMessage!!
                            else
                                errorMessage = "Some error occurred"
                        }

                        override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                            //For setting webview properties, if any. Check Customized Integration section for more details on this
                        }

                        override fun generateHash(
                            valueMap: HashMap<String, String?>,
                            hashGenerationListener: PayUHashGenerationListener
                        ) {
                            if ( valueMap.containsKey(CP_HASH_STRING)
                                && valueMap.containsKey(CP_HASH_STRING) != null
                                && valueMap.containsKey(CP_HASH_NAME)
                                && valueMap.containsKey(CP_HASH_NAME) != null) {

                                val hashData = valueMap[CP_HASH_STRING]
                                val hashName = valueMap[CP_HASH_NAME]

                                //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                                val hash: String? = "hash_from_server"
                                if (!TextUtils.isEmpty(hash)) {
                                    val dataMap: HashMap<String, String?> = HashMap()
                                    dataMap[hashName!!] = hash!!
                                    hashGenerationListener.onHashGenerated(dataMap)
                                }
                            }
                        }
                    })
            }
        }

        binding.editTextVoucherId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty()){
                    binding.textViewCouponApplied.visibility = View.GONE
                }
            }
        })


        binding.buttonApplyCoupon.setOnClickListener {
            if(binding.editTextVoucherId.text.isNullOrEmpty()){
                Snackbar.make(binding.root,"Please apply a coupon first",Snackbar.LENGTH_SHORT).show()
            }
            else{
                viewModel.getDiscount(binding.editTextVoucherId.text.toString())
                lifecycleScope.launch {
                    viewModel.discountGetFlow.collect {
                        when (it) {
                            is FragmentLiveClassesViewModel.DiscountGetEvent.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.root.isEnabled = false
                            }

                            is FragmentLiveClassesViewModel.DiscountGetEvent.Failure -> {
                                binding.progressBar.visibility = View.GONE
                                binding.root.isEnabled = true
                                Snackbar.make(binding.root,"Coupon Invalid!",Snackbar.LENGTH_SHORT).show()
                                binding.editTextAmount.setText(coursePrice.toString())
                                binding.textViewCouponApplied.visibility = View.GONE
                            }

                            is FragmentLiveClassesViewModel.DiscountGetEvent.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.root.isEnabled = true
                                val reducedPrice = it.voucher.amount.toInt()
                                var discountedPrice = coursePrice - reducedPrice
                                discountedPrice = max(0,discountedPrice)
                                binding.editTextAmount.setText(discountedPrice.toString())
                                binding.textViewCouponApplied.visibility = View.VISIBLE

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