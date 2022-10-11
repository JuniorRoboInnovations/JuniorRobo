package com.jrrobo.juniorroboapp.repository

import android.util.Log
import com.jrrobo.juniorroboapp.data.classroom.ClassroomChapters
import com.jrrobo.juniorroboapp.data.classroom.ClassroomDetails
import com.jrrobo.juniorroboapp.data.classroom.ClassroomSubjects
import com.jrrobo.juniorroboapp.network.JuniorRoboApi
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassroomRepository @Inject constructor(
    private val juniorRoboApi: JuniorRoboApi,
): MainClassroomRepository{

    // for logging purpose the TAG of the class name is created
    private val TAG: String = javaClass.simpleName

    override suspend fun classroomDetails(): NetworkRequestResource<List<ClassroomDetails>> {
        return try {

            // get the response from the API
            val response = juniorRoboApi.classroomDetails()

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
                Log.d(TAG, "classroomDetails: ${response.body()}")
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {

            // wrap the response around the NetworkRequestResource sealed class for ease of error handling
            // with the Error object
            NetworkRequestResource.Error(e.message ?: "An Error occurred")
        }
    }

    override suspend fun classroomSubjects(): NetworkRequestResource<List<ClassroomSubjects>> {
        return try {

            // get the response from the API
            val response = juniorRoboApi.classroomSubjects()

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
                Log.d(TAG, "classroomSubjects: ${response.body()}")
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {

            // wrap the response around the NetworkRequestResource sealed class for ease of error handling
            // with the Error object
            NetworkRequestResource.Error(e.message ?: "An Error occurred")
        }
    }

    override suspend fun classroomChapters(): NetworkRequestResource<List<ClassroomChapters>> {
        return try {

            // get the response from the API
            val response = juniorRoboApi.classroomChapters()

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
                Log.d(TAG, "classroomChapters: ${response.body()}")
                NetworkRequestResource.Error(response.message())
            }
        } catch (e: Exception) {

            // wrap the response around the NetworkRequestResource sealed class for ease of error handling
            // with the Error object
            NetworkRequestResource.Error(e.message ?: "An Error occurred")
        }
    }
}