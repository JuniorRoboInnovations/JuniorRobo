package com.jrrobo.juniorroboapp.repository

import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.data.course.CourseListItem
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource

/**
 * Interfaced the actual live classes repository for ease of testing, so that providing the mock object of
 * live classes repository testing can be done easily
 */
interface MainLiveClassesRepository {
    suspend fun getCourseCategories(): NetworkRequestResource<List<CourseListItem>>

    suspend fun getCourseGrades(courseId: Int): NetworkRequestResource<List<CourseGradeListItem>>

}