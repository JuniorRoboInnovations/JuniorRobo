package com.jrrobo.juniorroboapp.repository

import com.jrrobo.juniorroboapp.data.answer.AnswerItem
import com.jrrobo.juniorroboapp.data.answer.AnswerItemPost
import com.jrrobo.juniorroboapp.data.answer.AnswerItemPostResponse
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
import java.io.File

/**
 *  Mocked repository for ease of testing of AnswerRepository
 *  Interfaced the actual AnswerRepository
 */
interface MainAnswerRepository {

    suspend fun postAnswer(
        answerItemPost: AnswerItemPost
    ): NetworkRequestResource<AnswerItemPostResponse>

    suspend fun postAnswerImage(
        image: File
    ): NetworkRequestResource<String>

    suspend fun getAnswer(
        q_id: Int
    ): NetworkRequestResource<List<AnswerItem>>
}