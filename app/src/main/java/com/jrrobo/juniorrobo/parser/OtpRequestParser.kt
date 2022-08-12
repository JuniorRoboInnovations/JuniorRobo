package com.jrrobo.juniorrobo.parser

import org.json.JSONObject

// data class which holds the data for the OTP request
data class OtpRequest(
    val status: String,
    val message: String,
)

// data class which holds the data for the OTP response request
data class OtpResponse(
    val status: String,
    val message: String,
    val pkStudentId: Int,
)

// parser object, containing two methods to parse the response data
object OtpRequestParser {

    // for getting the OTP request after entering the contact number of the user
    fun otpRequest(jsonData: String): OtpRequest {
        return try {
            OtpRequest(
                JSONObject(jsonData).getString("status"),
                JSONObject(jsonData).getString("message")
            )
        } catch (e: Exception) {
            OtpRequest("Failure", "Something went wrong")
        }
    }

    // for getting the OTP response request after entering the contact number and OTP by user
    fun otpResponse(jsonData: String): OtpResponse {
        return try {
            OtpResponse(
                JSONObject(jsonData).getString("status"),
                JSONObject(jsonData).getString("message"),
                JSONObject(jsonData).getInt("data")
            )
        } catch (e: Exception) {
            OtpResponse(
                JSONObject(jsonData).getString("status"),
                JSONObject(jsonData).getString("message"),
                -1
            )
        }
    }
}