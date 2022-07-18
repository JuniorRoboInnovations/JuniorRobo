package com.jrrobo.juniorrobo.data.answer

// Answer POJO for POST request of answer for the question
data class AnswerItem(
    val snswer: String,
    val FkStudentId: Int?,
    val FkTeacherId: Int?,
    val FkQuestionId: Int
)
