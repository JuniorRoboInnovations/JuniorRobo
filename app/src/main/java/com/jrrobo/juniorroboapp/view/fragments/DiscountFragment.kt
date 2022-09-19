package com.jrrobo.juniorroboapp.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.jrrobo.juniorroboapp.databinding.FragmentDiscountBinding
import com.jrrobo.juniorroboapp.viewmodel.FragmentLiveClassesViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // getting the courseListItem sent from CourseListFragment
        coursePrice = DiscountFragmentArgs.fromBundle(requireArguments()).coursePrice
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