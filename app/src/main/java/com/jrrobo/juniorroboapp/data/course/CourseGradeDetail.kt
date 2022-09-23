package com.jrrobo.juniorroboapp.data.course

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseGradeDetail(
    val id:Int,
    val title:String,
    val description: String,
    val subject_covered:String,
    val image:String?,
    val fee:Int,
    val single_fee: Int,
    val duration: Long,
    val curriculum:String
):Parcelable