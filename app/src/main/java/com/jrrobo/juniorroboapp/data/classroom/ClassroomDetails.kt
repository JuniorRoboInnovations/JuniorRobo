package com.jrrobo.juniorroboapp.data.classroom

data class ClassroomDetails(
    val classroomId: Int,
    val liveTitle: String,
    val liveTeacherName: String,
    val liveTeacherImage: String,
    val recordedTitle: String,
    val recordedTeacherName: String,
    val recordedTeacherImage: String,
    val liveClassLink: String,
    val recordedClassLink: String
)
