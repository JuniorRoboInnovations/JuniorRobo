package com.jrrobo.juniorroboapp.data.profile

// Data class to hold the data of the user for POST and GET request
data class StudentProfileData(
    val pkStudentId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val mobile: String,
    val userImage: String,
    val city: String
)
