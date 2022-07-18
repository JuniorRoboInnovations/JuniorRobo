package com.jrrobo.juniorrobo.data.profile

// Data class to hold the data of the user for POST and GET request
data class StudentProfileData(
    val PkStudentId: Int,
    val FirstName: String,
    val LastName: String,
    val Email: String,
    val Mobile: String,
    val UserImage: String,
    val City: String
)
