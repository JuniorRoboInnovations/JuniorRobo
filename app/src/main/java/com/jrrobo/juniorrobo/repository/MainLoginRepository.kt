package com.jrrobo.juniorrobo.repository

import com.jrrobo.juniorrobo.utility.NetworkRequestResource

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

}