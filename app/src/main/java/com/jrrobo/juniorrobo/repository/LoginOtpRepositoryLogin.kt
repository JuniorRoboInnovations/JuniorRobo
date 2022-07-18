package com.jrrobo.juniorrobo.repository

import com.jrrobo.juniorrobo.network.JuniorRoboApi
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginOtpRepositoryLogin @Inject constructor(
    private val juniorRoboApi: JuniorRoboApi,
) : MainLoginRepository {

    // for logging purpose the TAG of the class name is created
    private val TAG = javaClass.simpleName

    // function for requesting the OTP after entering the contact number
    override suspend fun requestOtp(
        contactNumber: String,
    ): NetworkRequestResource<String> {
        return try {

            // get the response from the API
            val response = juniorRoboApi.requestOtp(contactNumber)

            // get the Scalar converter's body of the response provided by the API consisting of JSON Data
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

    // function for getting the OTP response after entering the contact number and OTP
    override suspend fun responseOtp(
        contactNumber: String,
        userOtp: String,
    ): NetworkRequestResource<String> {
        return try {

            // get the response from the API
            val response = juniorRoboApi.verifyOtp(contactNumber, userOtp)

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