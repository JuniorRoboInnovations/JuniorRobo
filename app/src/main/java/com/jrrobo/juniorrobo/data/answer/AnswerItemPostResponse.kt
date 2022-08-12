package com.jrrobo.juniorrobo.data.answer

data class AnswerItemPostResponse(
    val pkAnswerId: Int,
    val snswer: String,
    val fkStudentId: Int?,
    val fkTeacherId: Int?,
    val whoAnswered: String,
    val datetime: String,
    val isActive: Boolean,
    val isDelete: Boolean,
    val fkQuestionId: Int,
    val fkQuestion: String?,
    val fkStudent: String?,
    val fkTeacher: String?
)
