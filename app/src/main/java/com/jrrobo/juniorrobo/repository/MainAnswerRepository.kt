package com.jrrobo.juniorrobo.repository

import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.data.answer.AnswerItemPost
import com.jrrobo.juniorrobo.data.answer.AnswerItemPostResponse
import com.jrrobo.juniorrobo.utility.NetworkRequestResource

/**
 *  Mocked repository for ease of testing of AnswerRepository
 *  Interfaced the actual AnswerRepository
 */
interface MainAnswerRepository {

    suspend fun postAnswer(
        answerItemPost: AnswerItemPost
    ): NetworkRequestResource<AnswerItemPostResponse>

    suspend fun getAnswer(
        q_id: Int
    ): NetworkRequestResource<List<AnswerItem>>
}