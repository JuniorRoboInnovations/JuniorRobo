package com.jrrobo.juniorrobo.repository

import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategory
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemPostResponse
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemToAsk
import com.jrrobo.juniorrobo.utility.NetworkRequestResource

/**
 * Interfaced the actual class QuestionRepository to mock this object for testing purpose
 */
interface MainQuestionRepository {

    suspend fun postQuestionItem(
        questionItemToAsk: QuestionItemToAsk
    ): NetworkRequestResource<QuestionItemPostResponse>

    suspend fun getQuestionCategories(): NetworkRequestResource<List<QuestionCategoryItem>>

    suspend fun getAllQuestionsWithoutPaging(cat_id :Int?): NetworkRequestResource<List<QuestionItem>>

//    suspend fun getAllQuestionList(
//        cat_id: Int,
//        skip: Int,
//        take: Int
//    ): Pager()
}