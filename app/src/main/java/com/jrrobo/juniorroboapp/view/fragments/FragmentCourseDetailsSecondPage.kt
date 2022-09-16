package com.jrrobo.juniorroboapp.view.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.SpinnerAdapter
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.data.course.CourseGradeDetail
import com.jrrobo.juniorroboapp.databinding.FragmentCourseDetailBinding
import com.jrrobo.juniorroboapp.databinding.FragmentCourseDetailsSecondPage2Binding
import com.jrrobo.juniorroboapp.network.EndPoints
import com.jrrobo.juniorroboapp.utility.ScreenSliderAdapter
import com.jrrobo.juniorroboapp.view.adapter.CourseDetailItemAdapter
import com.jrrobo.juniorroboapp.viewmodel.FragmentLiveClassesViewModel
import kotlinx.coroutines.launch
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem as CourseGradeListItem

class FragmentCourseDetailsSecondPage(private val courseGradeListItem: CourseGradeListItem) : Fragment() {
    private val TAG: String = javaClass.simpleName

    // view binding object
    private var _binding: FragmentCourseDetailsSecondPage2Binding? = null

    // non null view binding object to avoid null checks using backing property
    private val binding: FragmentCourseDetailsSecondPage2Binding
        get() = _binding!!

    private val viewModel: FragmentLiveClassesViewModel by activityViewModels()

    private var subjectList = arrayListOf<Int>()

    private val subjectArray = arrayOf("Biology",
    "Physics",
    "Chemistry",
    "Maths",
    "Computers")

    private var selectedSubjects: BooleanArray = BooleanArray(subjectArray.size)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCourseDetailsSecondPage2Binding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this){

        }

        binding.scrollViewCourseDetail.setOnScrollChangeListener(View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY){
                binding.fabBookDemoButton.extend()
            }else if (scrollY < oldScrollY){
                binding.fabBookDemoButton.shrink()
            }
        })

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
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }


    }

    private fun showSubjectsDialog() {

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
        val dialogBinding = layoutInflater.inflate(R.layout.book_demo_layout, null)
        val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)

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

        val cancelButton = dialogBinding.findViewById<ImageView>(R.id.image_clear_demo)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun populateViews(courseGradeDetail: CourseGradeDetail) {
        binding.courseDetailAboutText.text = courseGradeDetail.description
        binding.courseDetailEnrolButton.text = "Enroll Now ₹" + courseGradeDetail.fee
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

        val subjectList : ArrayList<String> = ArrayList()
        val list = courseGradeDetail.subject_covered

        for(j in 0..10){
            subjectList.add(list)
        }

//        val dropDownListAdapter = ArrayAdapter(binding.subcourseSubjectInput.context,
//            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,subjectList)

//        binding.subjectsText.setAdapter(dropDownListAdapter)
//
//        val position = binding.subjectsText.listSelection.plus(1)
//        val subjects = subjectList[position]
//
//        binding.subcourseSubjectsSelectedText.append("" + subjects)

    }
}