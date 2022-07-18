package com.jrrobo.juniorrobo.repository

import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategory
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

    suspend fun getQuestionCategories(): NetworkRequestResource<QuestionCategory>

//    suspend fun getAllQuestionList(
//        cat_id: Int,
//        skip: Int,
//        take: Int
//    ): Pager()
}