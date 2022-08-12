package com.jrrobo.juniorrobo.data.answer

// Answer POJO for POST request of answer for the question
data class AnswerItem(
    val id : Int,
    val answer: String,
    val student: String?,
    val student_image: String,
    val date: String
)
