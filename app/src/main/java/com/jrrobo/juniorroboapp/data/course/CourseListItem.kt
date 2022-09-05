package com.jrrobo.juniorroboapp.data.course

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseListItem(
    val id: Int,
    val title: String,
    val image: String?
):Parcelable
