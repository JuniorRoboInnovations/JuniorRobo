package com.jrrobo.juniorroboapp.repository

import com.jrrobo.juniorroboapp.data.emailLogin.EmailRegisterData
import com.jrrobo.juniorroboapp.data.emailLogin.EmailRegisterResponse
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource

/**
 * Interfaced the actual login repository for ease of testing, so that providing the mock object of
 * login repository testing can be done easily
 */
interface MainLoginRepository {

    suspend fun requestOtp(
        contactNumber: String
    ): NetworkRequestResource<String>

    suspend fun responseOtp(
        contactNumber: String,
        userOtp: String
    ): NetworkRequestResource<String>

    suspend fun emailLogin(
        email: String,
        password: String
    ): NetworkRequestResource<String>

    suspend fun emailRegister(
       emailRegisterData: EmailRegisterData
    ): NetworkRequestResource<EmailRegisterResponse>
}