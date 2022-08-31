package com.jrrobo.juniorroboapp.data.questionitem


/**
 * For getting the response of the post request of the QuestionItem
 */
//data class QuestionItemPostResponse(
//    val item: Int,
//    val success: Boolean,
//    val message: String,
//    val errorCode: Int?
//)
data class QuestionItemPostResponse(
    val pkQuestionId: Int,
    val question: String,
    val questionSubtext: String,
    val fkStudentId: Int?,
    val fkTeacherId: Int?,
    val datetime: String,
    val isActive: Boolean,
    val isDelete: Boolean,
    val fkCategoryId: Int,
    val fkCategory: String?,
    val fkStudent: String?,
    val fkTeacher: String?
)