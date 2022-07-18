package com.jrrobo.juniorrobo.data.questionitem


/**
 * For getting the response of the post request of the QuestionItem
 */
data class QuestionItemPostResponse(
    val item: Int,
    val success: Boolean,
    val message: String,
    val errorCode: Int?
)