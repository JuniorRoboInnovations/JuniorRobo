package com.jrrobo.juniorroboapp.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.data.booking.BookingItem
import com.jrrobo.juniorroboapp.data.course.CourseGradeDetail
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.databinding.FragmentCourseDetailBinding
import com.jrrobo.juniorroboapp.network.EndPoints
import com.jrrobo.juniorroboapp.view.adapter.CourseDetailItemAdapter
import com.jrrobo.juniorroboapp.viewmodel.FragmentLiveClassesViewModel
import kotlinx.coroutines.launch


class CourseDetailFragment : Fragment() {

    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentCourseDetailBinding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentCourseDetailBinding
        get() = _binding!!

    // view model for this fragment
    private val viewModel: FragmentLiveClassesViewModel by activityViewModels()

    private lateinit var courseGradeListItem: CourseGradeListItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseGradeListItem = CourseDetailFragmentArgs.fromBundle(requireArguments()).courseGradeListItem
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCourseDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backImageButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.fabBookDemoButton.setOnClickListener {
            showDemoDialog()
        }


        binding.subcourseRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = CourseDetailItemAdapter(listOf(
                "Project/Activity Based Learning",
                "1:5 Batch Size",
                "Daily Reminder for Class",
                "Lowest Course Fee",
                "Monthly Subscription",
            ))
        }

        binding.courseDetailEnrolButton.setOnClickListener {
            postBookingItem()
        }

        viewModel.getCourseGradeDetails(courseGradeListItem.id)
        Log.d(TAG, "onViewCreated: calling getCourseDetails")
        lifecycleScope.launch {
            viewModel.courseGradeDetailsGetFlow.collect {
                when (it) {
                    is FragmentLiveClassesViewModel.CourseGradeDetailsGetEvent.Loading -> {

                    }

                    is FragmentLiveClassesViewModel.CourseGradeDetailsGetEvent.Failure -> {

                    }

                    is FragmentLiveClassesViewModel.CourseGradeDetailsGetEvent.Success -> {
                        // update the views
                        populateViews(it.courseGradeDetail)
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }
    }

    private fun postBookingItem() {
        var pkStudentId: Int = -1
        // request the primary key
        viewModel.getPkStudentIdPreference().observe(requireActivity(), Observer {
            // assign the primary key from the data store preference
            pkStudentId = it
        })

        viewModel.postBookingItem(
            BookingItem(
                0,
                "0",
                "0",
                courseGradeListItem.id,
                pkStudentId,
                0,
                null,
                "Pending"
            )
        )

        lifecycleScope.launch {
            viewModel.bookingPostFlow.collect {
                when (it) {
                    is FragmentLiveClassesViewModel.BookingItemPostEvent.Loading -> {
                        binding.courseDetailRootContainer.isClickable = false
                        binding.courseDetailProgressBar.visibility = View.VISIBLE
                    }

                    is FragmentLiveClassesViewModel.BookingItemPostEvent.Failure -> {
                        binding.courseDetailRootContainer.isClickable = true
                        binding.courseDetailProgressBar.visibility = View.GONE
                    }

                    is FragmentLiveClassesViewModel.BookingItemPostEvent.Success -> {
                        binding.courseDetailRootContainer.isClickable = true
                        binding.courseDetailProgressBar.visibility = View.GONE
                        Snackbar.make(binding.root,"Posted Booking Item Successfully!",Snackbar.LENGTH_SHORT).show()
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }


    }

    private fun setViewFlags() {
        binding.courseDetailRootContainer.setBackgroundColor(Color.parseColor("#ffffff"))
        binding.courseDetailRootContainer.background.alpha = 7
        val windowManager = requireActivity().window.attributes
        windowManager.dimAmount = 0.7f
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun clearViewFlags() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun showDemoDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.book_demo_layout,null)
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

    private fun populateViews(courseGradeDetail: CourseGradeDetail) {
        binding.subjectsCovered.text = courseGradeDetail.subject_covered
        binding.courseDetailAboutText.text = courseGradeDetail.description
        binding.courseDetailEnrolButton.text = "Enroll Now ₹" + courseGradeDetail.fee
        binding.courseDetailTitle.text = courseGradeDetail.title
        Glide.with(binding.root)
            .load(EndPoints.GET_IMAGE + "/course/" + courseGradeDetail.image)
            .into(binding.courseDetailImage)

        Glide.with(binding.root)
            .load(EndPoints.GET_IMAGE + "/course/" + courseGradeDetail.curriculum)
            .into(binding.courseDetailCurriculum)

        binding.courseDetailCurriculum.setOnClickListener {
            var dialogImagePreview: AlertDialog? = null

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            val customLayout: View = LayoutInflater.from(requireContext())
                .inflate(R.layout.cirriculum_image_dialog, null)
            val imageView = customLayout.findViewById<ImageView>(R.id.curriculumImageView)
            Glide.with(binding.root)
                .load(EndPoints.GET_IMAGE + "/course/" + courseGradeDetail.curriculum)
                .into(imageView)
            builder.setView(customLayout)

            val cancelButton = customLayout.findViewById<ImageView>(R.id.cancelImageView)
            cancelButton.setOnClickListener {
                dialogImagePreview?.dismiss()
            }

            dialogImagePreview = builder.create()

            dialogImagePreview.show()
        }
    }
}