package com.jrrobo.juniorrobo.network

import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategory
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemPostResponse
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemToAsk
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface JuniorRoboApi {


    // GET request for the OTP, taking entered number as query parameter
    @GET(EndPoints.APP_SMS)
    suspend fun requestOtp(
        @Query("number") contactNumber: String,
    ): Response<String>


    // GET request for verifying the entered OTP of number, taking number and the entered otp as query parameter
    @GET(EndPoints.APP_LOGIN)
    suspend fun verifyOtp(
        @Query("number") contactNumber: String,
        @Query("otp") otp: String,
    ): Response<String>


    // POST request for updating the profile of the student
    @POST(EndPoints.APP_STUDENT)
    suspend fun postUpdateProfile(
        @Body studentProfileData: StudentProfileData,
    ): Response<String>


    // GET request for updating the profile of the student
    @GET(EndPoints.APP_STUDENT)
    suspend fun getStudentProfile(
        @Query("id") id: Int,
    ): Response<String>


    // POST request to add the asked question to backend
    @POST(EndPoints.APP_QUESTION)
    suspend fun postQuestionItem(
        @Body questionItemToAsk: QuestionItemToAsk
    ): Response<QuestionItemPostResponse>


    // GET request to fetch all the question categories
    @GET(EndPoints.APP_CATEGORY)
    suspend fun getQuestionCategories(): QuestionCategory


    // POST request to post an answer for question with question ID
    @POST(EndPoints.APP_QUE_ANS)
    suspend fun postAnswerForQuestionId(
        @Body answerItem: AnswerItem
    ): Response<String>


    // POST request for uploading images and files
//    @Multipart
//    @POST
//    suspend fun postImage(
//        @Body
//    )


    // GET request to fetch all the question items to display in the question answer list
    @GET(EndPoints.APP_QUESTION)
    suspend fun getAllQuestionList(
        @Query("cat_id") cat_id: Int,
        @Query("skip") skip: Int,
        @Query("take") take: Int,
    ): QuestionItemsResponse
}