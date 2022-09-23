package com.jrrobo.juniorroboapp.view.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.databinding.FragmentViewPagerBinding
import com.jrrobo.juniorroboapp.view.onboarding.screens.*
import com.jrrobo.juniorroboapp.viewmodel.FragmentOnBoardLoginViewModel

class ViewPagerFragment : Fragment() {

    // view binding object
    private var _binding: FragmentViewPagerBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentViewPagerBinding
        get() = _binding!!

    // view model to get the on-boarding status of the application
    private val fragmentOnBoardLoginViewModel: FragmentOnBoardLoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment using view binding object
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        // create the list of fragment screens to pass to the ViewPagerAdapter
        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen(),
            FourthScreen(),
            FifthScreen(),
            SixthScreen()
        )

        // create the instance of ViewPagerAdapter
        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        // set the on-board view pager to created adapter
        binding.onboardViewPager.adapter = adapter

        // handling the page change callback to navigate back and forth screens of view pager
        binding.onboardViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // button clicks are handled for navigation of the view pager screens with respect
                // to their positions
                when (position) {
                    0 -> {
                        binding.buttonOnboardNext.setOnClickListener {
                            binding.onboardViewPager.currentItem = 1
                        }
                        binding.buttonOnboardSkip.setOnClickListener {
                            findNavController().navigate(R.id.action_viewPagerFragment_to_loginFragment)
                            fragmentOnBoardLoginViewModel.setOnBoardStatus(true)
                        }
                        binding.buttonOnboardSkip.visibility = View.VISIBLE
                        binding.buttonOnboardNext.visibility = View.VISIBLE
                        binding.buttonOnboardBack.visibility = View.INVISIBLE
                        binding.buttonOnboardFinish.visibility = View.INVISIBLE
                    }

                    1 -> {
                        binding.buttonOnboardNext.setOnClickListener {
                            binding.onboardViewPager.currentItem = 2
                        }
                        binding.buttonOnboardBack.setOnClickListener {
                            binding.onboardViewPager.currentItem = 0
                        }
                        binding.buttonOnboardSkip.visibility = View.INVISIBLE
                        binding.buttonOnboardBack.visibility = View.VISIBLE
                        binding.buttonOnboardFinish.visibility = View.INVISIBLE

                    }

                    2 -> {
                        binding.buttonOnboardNext.setOnClickListener {
                            binding.onboardViewPager.currentItem = 3
                        }
                        binding.buttonOnboardBack.setOnClickListener {
                            binding.onboardViewPager.currentItem = 1
                        }
                        binding.buttonOnboardSkip.visibility = View.INVISIBLE
                        binding.buttonOnboardFinish.visibility = View.INVISIBLE
                    }

                    3 -> {
                        binding.buttonOnboardNext.setOnClickListener {
                            binding.onboardViewPager.currentItem = 4
                        }
                        binding.buttonOnboardBack.setOnClickListener {
                            binding.onboardViewPager.currentItem = 2
                        }
                        binding.buttonOnboardSkip.visibility = View.INVISIBLE
                        binding.buttonOnboardFinish.visibility = View.INVISIBLE
                    }

                    4 -> {
                        binding.buttonOnboardNext.setOnClickListener {
                            binding.onboardViewPager.currentItem = 5
                        }
                        binding.buttonOnboardBack.setOnClickListener {
                            binding.onboardViewPager.currentItem = 3
                        }
                        binding.buttonOnboardSkip.visibility = View.INVISIBLE
                        binding.buttonOnboardNext.visibility = View.VISIBLE
                        binding.buttonOnboardFinish.visibility = View.INVISIBLE
                    }

                    5 -> {
                        binding.buttonOnboardBack.setOnClickListener {
                            binding.onboardViewPager.currentItem = 4
                        }
                        binding.buttonOnboardSkip.visibility = View.INVISIBLE
                        binding.buttonOnboardNext.visibility = View.INVISIBLE
                        binding.buttonOnboardFinish.visibility = View.VISIBLE

                        binding.buttonOnboardFinish.setOnClickListener {
                            findNavController().navigate(R.id.action_viewPagerFragment_to_loginFragment)
                            fragmentOnBoardLoginViewModel.setOnBoardStatus(true)
                        }
                    }
                }
            }
        })

        return binding.root
    }

    // set the view binding object to null upon destroying the view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}