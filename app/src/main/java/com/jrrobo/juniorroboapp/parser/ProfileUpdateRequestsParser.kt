package com.jrrobo.juniorroboapp.parser

import com.jrrobo.juniorroboapp.data.profile.StudentProfileData
import org.json.JSONObject

object ProfileUpdateRequestsParser {

    // POST request profile update parser for getting the profile data of the student
    fun parseStudentProfileUpdateResponse(jsonData: String): StudentProfileData {
        return StudentProfileData(
            JSONObject(jsonData).getJSONObject("item").getInt("pkStudentId"),
            JSONObject(jsonData).getJSONObject("item").getString("firstName"),
            JSONObject(jsonData).getJSONObject("item").getString("lastName"),
            JSONObject(jsonData).getJSONObject("item").getString("email"),
            JSONObject(jsonData).getJSONObject("item").getString("mobile"),
            JSONObject(jsonData).getJSONObject("item").getString("userImage"),
            JSONObject(jsonData).getJSONObject("item").getString("city")
        )
    }

    // GET request for student profile for getting and displaying data of student
    fun parseStudentProfileGetResponse(jsonData: String): StudentProfileData {
        return StudentProfileData(
            JSONObject(jsonData).getJSONObject("item").getInt("pkStudentId"),
            JSONObject(jsonData).getJSONObject("item").getString("firstName"),
            JSONObject(jsonData).getJSONObject("item").getString("lastName"),
            JSONObject(jsonData).getJSONObject("item").getString("email"),
            JSONObject(jsonData).getJSONObject("item").getString("mobile"),
            JSONObject(jsonData).getJSONObject("item").getString("userImage"),
            JSONObject(jsonData).getJSONObject("item").getString("city")
        )
    }
}