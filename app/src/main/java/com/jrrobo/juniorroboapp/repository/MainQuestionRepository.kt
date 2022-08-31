package com.jrrobo.juniorroboapp.repository

import com.jrrobo.juniorroboapp.data.offer.Offer
import com.jrrobo.juniorroboapp.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorroboapp.data.questionitem.QuestionItem
import com.jrrobo.juniorroboapp.data.questionitem.QuestionItemPostResponse
import com.jrrobo.juniorroboapp.data.questionitem.QuestionItemToAsk
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
import java.io.File

/**
 * Interfaced the actual class QuestionRepository to mock this object for testing purpose
 */
interface MainQuestionRepository {

    suspend fun postQuestionItem(
        questionItemToAsk: QuestionItemToAsk
    ): NetworkRequestResource<QuestionItemPostResponse>

    suspend fun postQuestionImage(
        image: File
    ): NetworkRequestResource<String>


    suspend fun getQuestionCategories(): NetworkRequestResource<List<QuestionCategoryItem>>

    suspend fun getAllQuestionsWithoutPaging(cat_id :Int?,keyword:String?,u_id:Int?): NetworkRequestResource<List<QuestionItem>>

    suspend fun getOffer() : NetworkRequestResource<Offer>

//    suspend fun getAllQuestionList(
//        cat_id: Int,
//        skip: Int,
//        take: Int
//    ): Pager()
}