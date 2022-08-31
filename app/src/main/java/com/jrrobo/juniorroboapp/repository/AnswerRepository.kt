package com.jrrobo.juniorroboapp.repository

import android.util.Log
import com.jrrobo.juniorroboapp.data.answer.AnswerItem
import com.jrrobo.juniorroboapp.data.answer.AnswerItemPost
import com.jrrobo.juniorroboapp.data.answer.AnswerItemPostResponse
import com.jrrobo.juniorroboapp.network.JuniorRoboApi
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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
    override suspend fun postAnswer(answerItemPost: AnswerItemPost): NetworkRequestResource<AnswerItemPostResponse> {
        return try {

            // get the response from the API
            val response = juniorRoboApi.postAnswerForQuestionId(answerItemPost)
            Log.e(TAG, "postAnswer: ${response.body()}")
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
            Log.d(TAG, "postAnswerItem: ${e.message}")
            // wrap the response around the NetworkRequestResource sealed class for ease of error handling
            // with the Error object
            NetworkRequestResource.Error(e.message ?: "Unable to post Answer")
        }
    }
    override suspend fun postAnswerImage(image: File): NetworkRequestResource<String> {
        return try {
            //creating request body for file
            val requestFile  = image.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            // MultipartBody.Part is used to send also the actual filename
            val body  = MultipartBody.Part.createFormData("file", image.name, requestFile)
            // get the response from the API
            val response = juniorRoboApi.postImage("answer",body)
            val result = response.body()
            // check whether the response was successful and is it null

            if (response.isSuccessful && result != null) {
                // wrap the response around the NetworkRequestResource sealed class for ease of error handling
                // with the Success object
                Log.d(TAG, "uploadImage: $result")
                NetworkRequestResource.Success(result)
            } else {

                // wrap the response around the NetworkRequestResource sealed class for ease of error handling
                // with the Error object
                Log.d(TAG, "uploadImage: ${response.message()}")
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {

            // wrap the response around the NetworkRequestResource sealed class for ease of error handling
            // with the Error object
            Log.d(TAG, "uploadImage: ${e.message}")
            NetworkRequestResource.Error(e.message ?: "Unable to upload the image.")
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