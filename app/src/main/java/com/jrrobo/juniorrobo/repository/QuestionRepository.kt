package com.jrrobo.juniorrobo.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategory
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemPagingSource
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemPostResponse
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemToAsk
import com.jrrobo.juniorrobo.network.JuniorRoboApi
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * QuestionRepository for GET and POST request of the question items
 */
@Singleton
class QuestionRepository @Inject constructor(
    private val juniorRoboApi: JuniorRoboApi
) : MainQuestionRepository {

    private val TAG: String = javaClass.simpleName

    override suspend fun postQuestionItem(
        questionItemToAsk: QuestionItemToAsk
    ): NetworkRequestResource<QuestionItemPostResponse> {
        return try {
            val response = juniorRoboApi.postQuestionItem(questionItemToAsk)

            val result = response.body()

            if (response.isSuccessful && result != null) {
                NetworkRequestResource.Success(result)
            } else {
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkRequestResource.Error(e.message ?: "Unable to post question")
        }
    }

    override suspend fun getQuestionCategories(): NetworkRequestResource<QuestionCategory> {
        return try {
            val response = juniorRoboApi.getQuestionCategories()

            if (response != null) {
                Log.d(TAG, response.toString())
                NetworkRequestResource.Success(response)
            } else {
                Log.d(TAG, response.toString())
                NetworkRequestResource.Error(response)
            }
        } catch (e: Exception) {
            NetworkRequestResource.Error(e.message ?: "Unable to get question categories")
        }
    }

    fun getAllQuestionList(
        cat_id: Int,
        skip: Int,
    ) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { QuestionItemPagingSource(juniorRoboApi, cat_id) }
    ).liveData
}