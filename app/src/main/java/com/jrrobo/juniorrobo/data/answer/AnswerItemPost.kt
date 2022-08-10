package com.jrrobo.juniorrobo.data.answer

data class AnswerItemPost(
    val snswer: String,
    val FkStudentId: Int,
    val FkTeacherId: Int,
    val FkQuestionId: Int
)
