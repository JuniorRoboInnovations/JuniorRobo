package com.jrrobo.juniorrobo.data.questionitem

/**
 * for POST request of the question item
 */
data class QuestionItemToAsk(
    val Question: String,
    val QuestionSubtext: String,
    val QuestionType: String,
    val FkStudentId: Int,
    val FkTeacherId: Int?,
    val FkCategoryId: Int,
    val QuestionImage: String?
)