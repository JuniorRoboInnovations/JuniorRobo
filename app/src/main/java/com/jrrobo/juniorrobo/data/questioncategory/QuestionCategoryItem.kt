package com.jrrobo.juniorrobo.data.questioncategory

import com.google.gson.annotations.SerializedName

// This is the nested object for QuestionCategory POJO for list of the question categories
data class QuestionCategoryItem(
    @SerializedName("id")
    val pkCategoryId: Int,
    @SerializedName("title")
    val categoryTitle: String
)
