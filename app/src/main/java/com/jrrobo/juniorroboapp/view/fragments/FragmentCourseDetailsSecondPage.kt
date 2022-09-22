package com.jrrobo.juniorroboapp.view.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.hbb20.CountryCodePicker
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.data.booking.BookingDemoItem
import com.jrrobo.juniorroboapp.data.course.CourseGradeDetail
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.databinding.FragmentCourseDetailsSecondPage2Binding
import com.jrrobo.juniorroboapp.network.EndPoints
import com.jrrobo.juniorroboapp.view.adapter.CourseDetailItemAdapter
import com.jrrobo.juniorroboapp.viewmodel.FragmentLiveClassesViewModel
import kotlinx.coroutines.launch

class FragmentCourseDetailsSecondPage(private val courseGradeListItem: CourseGradeListItem) : Fragment() {
    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentCourseDetailsSecondPage2Binding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentCourseDetailsSecondPage2Binding
        get() = _binding!!

    private val viewModel: FragmentLiveClassesViewModel by activityViewModels()

//    private var subjectList = arrayListOf<Int>()

    // courseGradeDetail object to get the course details
    private lateinit var courseGradeDetail : CourseGradeDetail

//    private val subjectArray = arrayOf("Biology",
//        "Physics",
//        "Chemistry",
//        "Maths",
//        "Computers")
//
//    private var selectedSubjects: BooleanArray = BooleanArray(subjectArray.size)
//    private var singleFee = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCourseDetailsSecondPage2Binding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.courseDetailEnrolButton.setOnClickListener {
            findNavController().navigate(CourseDetailViewPagerFragmentDirections.actionCourseDetailViewPagerFragmentToDiscountFragment(courseGradeDetail.fee))
        }

        binding.scrollViewCourseDetail.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.fabBookDemoButton.extend()
            } else if (scrollY < oldScrollY) {
                binding.fabBookDemoButton.shrink()
            }
        }

        binding.subcourseHoursText.doOnTextChanged { text, start, before, count ->
            var noh = 0
            if (text.toString().isNotEmpty()) {
                noh = text.toString().toInt()
            }
            val price = noh * courseGradeDetail.single_fee

            binding.amountText.text = "Your Total Amount is: $price"
            binding.courseDetailEnrolButton.text = "Enroll Now ₹$price"
        }

        binding.subjectsCard.setOnClickListener {
            showSubjectsDialog()
        }

        binding.fabBookDemoButton.setOnClickListener {
            showDemoDialog()
        }

        binding.subcourseRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = CourseDetailItemAdapter(
                listOf(
                    "Project/Activity Based Learning",
                    "1:5 Batch Size",
                    "Daily Reminder for Class",
                    "Lowest Course Fee",
                    "Monthly Subscription",
                )
            )
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
                        courseGradeDetail = it.courseGradeDetail
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }


    }

    private fun showSubjectsDialog() {

        val subjectList = arrayListOf<Int>()

        // courseGradeDetail object to get the course details

        val subjectArray = courseGradeDetail.subject_covered.split(',').toTypedArray()

        val selectedSubjects = BooleanArray(subjectArray.size)

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Select Subjects")
        builder.setCancelable(false)

        builder.setMultiChoiceItems(subjectArray, selectedSubjects, DialogInterface.OnMultiChoiceClickListener{
                dialog, which, isChecked ->
            if (isChecked){
                subjectList.add(which)
            }else{
                subjectList.remove(which)
            }

        }).setPositiveButton("OK", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

                val stringBuilder: StringBuilder = StringBuilder("")
                for (i in 0 until subjectList.size){

                    stringBuilder.append(subjectArray[subjectList[i]])

                    if (i != subjectList.size - 1){
                        stringBuilder.append(", ")
                    }


                    binding.subjectsText.text = stringBuilder.toString()
                }
            }

        }).setNegativeButton("Cancel", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.dismiss()
            }
        }).setNeutralButton("Clear All", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

                for (i in selectedSubjects.indices){
                    selectedSubjects[i] = false
                    subjectList.clear()
                    binding.subjectsText.text = ""
                }
            }
        })

        builder.show()
    }

    private fun showDemoDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.book_demo_layout,null)
        val dialog = Dialog(requireContext(),android.R.style.Theme_Translucent_NoTitleBar)

        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        lp.dimAmount = 0.7f

        dialog.window!!.attributes = lp
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        dialog.show()

        dialogBinding.findViewById<EditText>(R.id.edit_text_course_name).setText(courseGradeListItem.title)

        dialogBinding.findViewById<ImageView>(R.id.image_clear_demo).setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.findViewById<Button>(R.id.book_demo_button).setOnClickListener {

            postBookingDemoItem(dialog,
                BookingDemoItem(
                    dialogBinding.findViewById<EditText>(R.id.edit_text_student_name).text.toString(),
                    dialogBinding.findViewById<EditText>(R.id.edit_text_father_name).text.toString(),
                    dialogBinding.findViewById<EditText>(R.id.edit_text_email).text.toString(),
                    dialogBinding.findViewById<CountryCodePicker>(R.id.demo_country_code_picker).selectedCountryCode.trim()
                            + dialogBinding.findViewById<EditText>(R.id.demo_phone_number).text.toString(),
                    null,
                    dialogBinding.findViewById<EditText>(R.id.edit_text_course_name).text.toString(),
                    null
                )
            )
        }
    }

    private fun postBookingDemoItem(dialog: Dialog, bookingDemoItem: BookingDemoItem) {
        if(bookingDemoItem.studentname.isNullOrEmpty()
            || bookingDemoItem.fathername.isNullOrEmpty()
            || bookingDemoItem.email.isNullOrEmpty()
            || bookingDemoItem.mobile.isNullOrEmpty()
        ){
            Toast.makeText(requireContext(),"Please fill the details to book demo!", Toast.LENGTH_SHORT).show()
        }
        else{
            Log.d(TAG, "postBookingDemoItem: $bookingDemoItem")
            viewModel.postBookingDemoItem(bookingDemoItem)
            lifecycleScope.launch {
                viewModel.bookingDemoItemPostFlow.collect {
                    when (it) {
                        is FragmentLiveClassesViewModel.BookingDemoItemPostEvent.Loading -> {
                            binding.scrollViewCourseDetail.isClickable = false
                            binding.courseDetailProgressBar.visibility = View.VISIBLE
                        }

                        is FragmentLiveClassesViewModel.BookingDemoItemPostEvent.Failure -> {
                            Log.d(TAG, "postBookingDemoItem: ${it.errorText}")
                            binding.scrollViewCourseDetail.isClickable = true
                            binding.courseDetailProgressBar.visibility = View.GONE
                        }

                        is FragmentLiveClassesViewModel.BookingDemoItemPostEvent.Success -> {
                            binding.scrollViewCourseDetail.isClickable = true
                            binding.courseDetailProgressBar.visibility = View.GONE
                            Snackbar.make(binding.root,"Booking demo for this course successful!",
                                Snackbar.LENGTH_LONG).show()
                            dialog.dismiss()
                        }
                        else -> {
                            Unit
                        }
                    }
                }
            }
        }

    }

    private fun populateViews(courseGradeDetail: CourseGradeDetail) {
        binding.courseDetailAboutText.text = courseGradeDetail.description


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