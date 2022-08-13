package com.jrrobo.juniorrobo.data.answer

/**
 * for POST request of answer item
 */
data class AnswerItemPost(
    val snswer: String,
    val FkStudentId: Int?,
    val FkTeacherId: Int?,
    val FkQuestionId: Int
)
