package com.jrrobo.juniorrobo.repository

import android.util.Log
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.network.JuniorRoboApi
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Answer Repository for GET and POST requesting the answers for each QuestionItem
 */
@Singleton
class AnswerRepository @Inject constructor(
    private val juniorRoboApi: JuniorRoboApi
) : MainAnswerRepository {
    private val TAG: String = javaClass.simpleName
    override suspend fun postAnswer(answerItem: AnswerItem): NetworkRequestResource<String> {
        return try {

            // get the response from the API
            val response = juniorRoboApi.postAnswerForQuestionId(answerItem)

            // get the Scalar converter's body of the response provided by the API
            val result = response.body()

            // check whether the response was successful and is it null
            if (response.isSuccessful && result != null) {

                // wrap the response around the NetworkRequestResource sealed class for ease of error handling
                // with the Success object
                NetworkRequestResource.Success(result)
            } else {

                // wrap the response around the NetworkRequestResource sealed class for ease of error handling
                // with the Error object
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {

            // wrap the response around the NetworkRequestResource sealed class for ease of error handling
            // with the Error object
            NetworkRequestResource.Error(e.message ?: "Unable to update the profile")
        }
    }

    override suspend fun getAnswer(q_id: Int): NetworkRequestResource<List<AnswerItem>> {
        return try {
            Log.d(TAG, "getAnswer: before api call")
            val response = juniorRoboApi.getAnswerList(q_id,0,10)
            Log.d(TAG, "getAnswer: after api call")

            val result = response.body()

            if (result != null) {
                Log.d(TAG,"getAnswer---->"+ result.toString())
                NetworkRequestResource.Success(result)
            } else {
                Log.d(TAG, response.toString())
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkRequestResource.Error(e.message ?: "Unable to get Answers")
        }
    }


}