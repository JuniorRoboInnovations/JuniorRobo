package com.jrrobo.juniorroboapp.data.questionitem

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Made Parcelable to share this QuestionItem object
 * from QuestionAnswerFragment to QuestionDetails activity
 * */
//@Parcelize
//data class QuestionItem(
//    @SerializedName("id")
//    val pkQuestionId: Int,
//    val question: String,
//    @SerializedName("question_sub_text")
//    val questionSubtext: String,
//    val questionType: String,
//    val fkStudentId: Int?,
//    val fkTeacherId: Int?,
//    @SerializedName("date")
//    val datetime: String,
//    val fkCategoryId: Int
//) : Parcelable
@Parcelize
data class QuestionItem(
    val id:Int,
    val question:String,
    val question_sub_text:String,
    val image:String?,
    val date:String
):Parcelable