package com.jrrobo.juniorrobo.repository

import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.utility.NetworkRequestResource

/**
 * Interfaced the actual profile update repository for ease of testing, so that providing the mock object of
 * profile update repository testing can be done easily
 */
interface MainProfileRepository {

    suspend fun updateProfile(
        studentProfileData: StudentProfileData
    ): NetworkRequestResource<String>

    suspend fun getStudentProfile(
        id: Int
    ): NetworkRequestResource<String>
}