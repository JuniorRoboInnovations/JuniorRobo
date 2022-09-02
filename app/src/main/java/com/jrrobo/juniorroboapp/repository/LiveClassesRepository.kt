package com.jrrobo.juniorroboapp.repository

import android.util.Log
import com.jrrobo.juniorroboapp.data.course.CourseListItem
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
}