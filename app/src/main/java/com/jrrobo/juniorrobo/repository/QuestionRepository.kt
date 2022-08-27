package com.jrrobo.juniorrobo.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.jrrobo.juniorrobo.data.offer.Offer
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategory
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemPagingSource
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemPostResponse
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemToAsk
import com.jrrobo.juniorrobo.network.JuniorRoboApi
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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
            Log.d(TAG, "postQuestionItem: ${response.body()} ")
            if (response.isSuccessful && result != null) {
                NetworkRequestResource.Success(result)
            } else {
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            Log.d(TAG, "postQuestionItem: ${e.message}")
            NetworkRequestResource.Error(e.message ?: "Unable to post question")
        }
    }


    override suspend fun postQuestionImage(image: File): NetworkRequestResource<String> {
        return try {
            //creating request body for file
            val requestFile  = image.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            // MultipartBody.Part is used to send also the actual filename
            val body  = MultipartBody.Part.createFormData("file", image.name, requestFile)
            // get the response from the API
            val response = juniorRoboApi.postImage("question",body)
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

    override suspend fun getQuestionCategories(): NetworkRequestResource<List<QuestionCategoryItem>> {
        return try {
            val response = juniorRoboApi.getQuestionCategories()
            
            
            val result = response.body()

            Log.d(TAG, "getQuestionCategories: ${result}")
            
            if (result != null) {
                NetworkRequestResource.Success(result)
            } else {
                Log.d(TAG, response.toString())
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkRequestResource.Error(e.message ?: "Unable to get question categories")
        }
    }

    override suspend fun getAllQuestionsWithoutPaging(cat_id: Int?,keyword:String?,u_id:Int?): NetworkRequestResource<List<QuestionItem>> {
        return try {
            val response = juniorRoboApi.getAllQuestionListWithoutPaging(u_id,cat_id,0,300,keyword)

            val result = response.body()

            if (result != null) {
                NetworkRequestResource.Success(result)
            } else {
                Log.d(TAG, response.toString())
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkRequestResource.Error(e.message ?: "Unable to get questions")
        }
    }

    override suspend fun getOffer(): NetworkRequestResource<Offer> {
        return try {
            val response = juniorRoboApi.getOffer()
            val result = response.body()

            if (result != null) {
                NetworkRequestResource.Success(result)
            } else {
                Log.d(TAG, response.toString())
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkRequestResource.Error(e.message ?: "Unable to get questions")
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