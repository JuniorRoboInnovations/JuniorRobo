package com.jrrobo.juniorroboapp.repository

import com.jrrobo.juniorroboapp.data.profile.StudentProfileData
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
import okhttp3.ResponseBody
import java.io.File

/**
 * Interfaced the actual profile update repository for ease of testing, so that providing the mock object of
 * profile update repository testing can be done easily
 */
interface MainProfileRepository {

    suspend fun updateProfile(
        studentProfileData: StudentProfileData
    ): NetworkRequestResource<StudentProfileData>

    suspend fun getStudentProfile(
        id: Int
    ): NetworkRequestResource<StudentProfileData>

    suspend fun uploadImage(
        image: File)
    : NetworkRequestResource<String>

    suspend fun getStudentImage(
        imageName: String
    ):NetworkRequestResource<ResponseBody>
}