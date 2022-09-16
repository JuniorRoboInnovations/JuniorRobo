package com.jrrobo.juniorroboapp.network

import com.jrrobo.juniorroboapp.data.answer.AnswerItem
import com.jrrobo.juniorroboapp.data.answer.AnswerItemPost
import com.jrrobo.juniorroboapp.data.answer.AnswerItemPostResponse
import com.jrrobo.juniorroboapp.data.booking.BookingDemoItem
import com.jrrobo.juniorroboapp.data.booking.BookingDemoItemPostResponse
import com.jrrobo.juniorroboapp.data.booking.BookingItem
import com.jrrobo.juniorroboapp.data.course.CourseGradeDetail
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.data.course.CourseListItem
import com.jrrobo.juniorroboapp.data.emailLogin.EmailRegisterData
import com.jrrobo.juniorroboapp.data.emailLogin.EmailRegisterResponse
import com.jrrobo.juniorroboapp.data.offer.Offer
import com.jrrobo.juniorroboapp.data.profile.StudentProfileData
import com.jrrobo.juniorroboapp.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorroboapp.data.questionitem.QuestionItem
import com.jrrobo.juniorroboapp.data.questionitem.QuestionItemPostResponse
import com.jrrobo.juniorroboapp.data.questionitem.QuestionItemToAsk
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

    //GET request for login with email
    @GET(EndPoints.APP_EMAIL_LOGIN)
    suspend fun emailLogin(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<String>

    //
    @POST(EndPoints.APP_REGISTER_EMAIL)
    suspend fun emailRegister(
        @Body emailRegisterData: EmailRegisterData
    ): Response<EmailRegisterResponse>

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

    // GET request to fetch all course categories
    @GET(EndPoints.APP_COURSE)
    suspend fun getCourseCategories() : Response<List<CourseListItem>>

    // GET request to fetch all grades of a course
    @GET(EndPoints.APP_GRADE+"/{course_id}")
    suspend fun getCourseGrades(
        @Path("course_id") course_id: Int
    ): Response<List<CourseGradeListItem>>

    // GET request to fetch details of a course
    @GET(EndPoints.APP_GRADE+"/GetDetails/{id}")
    suspend fun getCourseGradeDetails(
        @Path("id") id: Int
    ): Response<CourseGradeDetail>

    @POST(EndPoints.APP_BOOKING)
    suspend fun postBookingItem(
        @Body bookingItem: BookingItem
    ): Response<Int>

    @POST(EndPoints.STUDENT_QUERY)
    suspend fun postBookingDemo(
        @Body bookingDemoItem: BookingDemoItem
    ): Response<BookingDemoItemPostResponse>

}