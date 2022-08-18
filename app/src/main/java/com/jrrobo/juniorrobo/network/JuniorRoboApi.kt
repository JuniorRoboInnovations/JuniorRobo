package com.jrrobo.juniorrobo.network

import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.data.answer.AnswerItemPost
import com.jrrobo.juniorrobo.data.answer.AnswerItemPostResponse
import com.jrrobo.juniorrobo.data.offer.Offer
import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategory
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemPostResponse
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemToAsk
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemsResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

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
    ): Response<StudentProfileData>


    // GET request for updating the profile of the student
    @GET(EndPoints.APP_STUDENT)
    suspend fun getStudentProfile(
        @Query("id") id: Int,
    ): Response<StudentProfileData>


    // POST request to add the asked question to backend
    @POST(EndPoints.APP_QUESTION)
    suspend fun postQuestionItem(
        @Body questionItemToAsk: QuestionItemToAsk
    ): Response<QuestionItemPostResponse>


    // GET request to fetch all the question categories
    @GET(EndPoints.APP_CATEGORY)
    suspend fun getQuestionCategories(): Response<List<QuestionCategoryItem>>


    // POST request to post an answer for question with question ID
    @POST(EndPoints.APP_QUE_ANS)
    suspend fun postAnswerForQuestionId(
        @Body answerItemPost: AnswerItemPost
    ): Response<AnswerItemPostResponse>


//     POST request for uploading images
    @Multipart
    @POST(EndPoints.IMAGE_UPLOAD)
    suspend fun postImage(
        @Query("type") type: String,
        @Part image : MultipartBody.Part
    ): Response<String>

    // GET request to fetch the image data from server
    @GET(EndPoints.GET_IMAGE+"/{imageName}")
    suspend fun getImage(
        @Path("imageName") imageName: String
    ): Response<ResponseBody>


    // GET request to fetch all the question items to display in the question answer list
    @GET(EndPoints.APP_QUESTION)
    suspend fun getAllQuestionList(
        @Query("cat_id") cat_id: Int = 0,
        @Query("skip") skip: Int,
        @Query("take") take: Int,
    ): Response<List<QuestionItem>>


    // GET request to fetch all the question items to display in the question answer list without paging
    @GET(EndPoints.APP_QUESTION)
    suspend fun getAllQuestionListWithoutPaging(
        @Query("u_id") u_id: Int?,
        @Query("cat_id") cat_id: Int?,
        @Query("skip") skip: Int?,
        @Query("take") take: Int?,
        @Query("keyword") keyword: String?
    ): Response<List<QuestionItem>>


    @GET(EndPoints.APP_QUE_ANS)
    suspend fun getAnswerList(
        @Query("q_id") q_id: Int,
        @Query("skip") skip: Int,
        @Query("take") take: Int
    ): Response<List<AnswerItem>>

    @GET(EndPoints.APP_OFFER)
    suspend fun getOffer(): Response<Offer>

}