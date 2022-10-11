package com.jrrobo.juniorroboapp.repository

import com.jrrobo.juniorroboapp.data.classroom.ClassroomChapters
import com.jrrobo.juniorroboapp.data.classroom.ClassroomDetails
import com.jrrobo.juniorroboapp.data.classroom.ClassroomSubjects
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource

interface MainClassroomRepository {

    suspend fun classroomDetails() : NetworkRequestResource<List<ClassroomDetails>>

    suspend fun classroomSubjects() : NetworkRequestResource<List<ClassroomSubjects>>

    suspend fun classroomChapters() : NetworkRequestResource<List<ClassroomChapters>>
}