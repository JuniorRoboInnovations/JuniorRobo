package com.jrrobo.juniorrobo.repository

import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.utility.NetworkRequestResource

/**
 *  Mocked repository for ease of testing of AnswerRepository
 *  Interfaced the actual AnswerRepository
 */
interface MainAnswerRepository {

    suspend fun postAnswer(
        answerItem: AnswerItem
    ): NetworkRequestResource<String>
}