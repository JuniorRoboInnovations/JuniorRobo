package com.jrrobo.juniorrobo.repository

import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.network.JuniorRoboApi
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Profile repository for POST and GET requests of the Profile data of the user
 */
@Singleton
class ProfileRepository @Inject constructor(
    private val juniorRoboApi: JuniorRoboApi,
) : MainProfileRepository {

    // for logging purpose the TAG of the class name is created
    private val TAG: String = javaClass.simpleName

    // function for requesting the OTP after entering the contact number
    override suspend fun updateProfile(
        studentProfileData: StudentProfileData,
    ): NetworkRequestResource<String> {
        return try {

            // get the response from the API
            val response = juniorRoboApi.postUpdateProfile(studentProfileData)

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

    // for GET request of the API to fetch the already existing student with his/her primary key
    override suspend fun getStudentProfile(id: Int): NetworkRequestResource<String> {
        return try {

            // get the response from the API
            val response = juniorRoboApi.getStudentProfile(id)

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
            NetworkRequestResource.Error(e.message ?: "An Error occurred")
        }
    }
}