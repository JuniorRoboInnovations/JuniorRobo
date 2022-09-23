package com.jrrobo.juniorroboapp.repository

import android.util.Log
import com.jrrobo.juniorroboapp.data.booking.BookingDemoItem
import com.jrrobo.juniorroboapp.data.booking.BookingDemoItemPostResponse
import com.jrrobo.juniorroboapp.data.booking.BookingItem
import com.jrrobo.juniorroboapp.data.course.CourseGradeDetail
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.data.course.CourseListItem
import com.jrrobo.juniorroboapp.data.voucher.Voucher
import com.jrrobo.juniorroboapp.network.JuniorRoboApi
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveClassesRepository @Inject constructor(
    private val juniorRoboApi: JuniorRoboApi
) :MainLiveClassesRepository{

    private val TAG: String = javaClass.simpleName

    override suspend fun getCourseCategories(): NetworkRequestResource<List<CourseListItem>> {
        return try {
            val response = juniorRoboApi.getCourseCategories()

            val result = response.body()

            if (response.isSuccessful && result != null) {
                NetworkRequestResource.Success(result)
            } else {
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            Log.d(TAG, "getCourseCategories: ${e.message}")
            NetworkRequestResource.Error(e.message ?: "Unable to get course categories")
        }
    }

    override suspend fun getCourseGrades(courseId: Int): NetworkRequestResource<List<CourseGradeListItem>> {
        return try {
            val response = juniorRoboApi.getCourseGrades(courseId)

            val result = response.body()

            if (response.isSuccessful && result != null) {
                NetworkRequestResource.Success(result)
            } else {
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            Log.d(TAG, "getCourseGrades: ${e.message}")
            NetworkRequestResource.Error(e.message ?: "Unable to get course grades")
        }
    }

    override suspend fun getCourseGradeDetails(id: Int): NetworkRequestResource<CourseGradeDetail> {
        return try {
            val response = juniorRoboApi.getCourseGradeDetails(id)

            val result = response.body()

            if (response.isSuccessful && result != null) {
                NetworkRequestResource.Success(result)
            } else {
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            Log.d(TAG, "getCourseGradeDetails: ${e.message}")
            NetworkRequestResource.Error(e.message ?: "Unable to get course details")
        }
    }

    override suspend fun postBookingItem(bookingItem: BookingItem): NetworkRequestResource<Int> {
        return try {
            val response = juniorRoboApi.postBookingItem(bookingItem)

            val result = response.body()

            if (response.isSuccessful && result != null) {
                NetworkRequestResource.Success(result)
            } else {
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            Log.d(TAG, "postBookingItem: ${e.message}")
            NetworkRequestResource.Error(e.message ?: "Unable to post booking item")
        }
    }

    override suspend fun postBookingDemoItem(bookingDemoItem: BookingDemoItem): NetworkRequestResource<BookingDemoItemPostResponse> {
        return try {
            val response = juniorRoboApi.postBookingDemo(bookingDemoItem)

            val result = response.body()
            if (response.isSuccessful && result != null) {
                Log.d(TAG, "postBookingDemoItem: ${result.toString()}")
                NetworkRequestResource.Success(result)
            } else {
                Log.d(TAG, "postBookingDemoItem: ${response.message()}")
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            Log.d(TAG, "postBookingItem: ${e.message}")
            NetworkRequestResource.Error(e.message ?: "Unable to post booking demo")
        }
    }

    override suspend fun getDiscount(coupon: String): NetworkRequestResource<Voucher> {
        return try {
            val response = juniorRoboApi.getDiscountAmount(coupon)

            val result = response.body()
            if (response.isSuccessful && result != null) {
                NetworkRequestResource.Success(result)
            } else {
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkRequestResource.Error(e.message ?: "Unable to get discount")
        }
    }
}