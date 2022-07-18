package com.jrrobo.juniorrobo.data.questionitem

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Made Parcelable to share this QuestionItem object
 * from QuestionAnswerFragment to QuestionDetails activity
 * */
@Parcelize
data class QuestionItem(
    val pkQuestionId: Int,
    val question: String,
    val questionSubtext: String,
    val questionType: String,
    val fkStudentId: Int?,
    val fkTeacherId: Int?,
    val datetime: String,
    val fkCategoryId: Int
) : Parcelable
