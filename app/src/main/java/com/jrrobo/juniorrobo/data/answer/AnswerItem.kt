package com.jrrobo.juniorrobo.data.answer

import java.util.*

// Answer POJO for POST request of answer for the question
data class AnswerItem(
    val id : Int,
    val answer: String,
    val student: Int?,
    val student_image: String,
    val date: Date
)
