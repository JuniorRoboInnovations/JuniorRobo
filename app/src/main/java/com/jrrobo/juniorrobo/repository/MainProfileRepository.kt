package com.jrrobo.juniorrobo.repository

import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import okhttp3.ResponseBody
import java.io.File

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

    suspend fun uploadImage(
        image: File)
    : NetworkRequestResource<String>

    suspend fun getStudentImage(
        imageName: String
    ):NetworkRequestResource<ResponseBody>
}