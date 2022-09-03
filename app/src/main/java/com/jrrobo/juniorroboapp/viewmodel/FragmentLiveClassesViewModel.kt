package com.jrrobo.juniorroboapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.data.course.CourseListItem
import com.jrrobo.juniorroboapp.repository.LiveClassesRepository
import com.jrrobo.juniorroboapp.utility.DataStorePreferencesManager
import com.jrrobo.juniorroboapp.utility.DispatcherProvider
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentLiveClassesViewModel @Inject constructor(
    private val repository: LiveClassesRepository,
    private val dataStorePreferencesManager: DataStorePreferencesManager,
    private val dispatchers: DispatcherProvider
): ViewModel() {

    private val TAG: String = javaClass.simpleName

    /**
     * Course get event
     * Request type: GET
     */
    sealed class CourseListGetEvent {
        class Success(val courseList: List<CourseListItem>) : CourseListGetEvent()
        class Failure(val errorText: String) : CourseListGetEvent()
        object Loading : CourseListGetEvent()
        object Empty : CourseListGetEvent()
    }

    private val _courseListGetFlow = MutableStateFlow<CourseListGetEvent>(CourseListGetEvent.Empty)
    val courseListGetFlow: MutableStateFlow<CourseListGetEvent> = _courseListGetFlow


    fun getCourseCategories(){
        // launch coroutine to request for course category list
        viewModelScope.launch(dispatchers.io) {

            // set the profile get event to loading state
            _courseListGetFlow.value = CourseListGetEvent.Loading

            when (val courseCategories = repository.getCourseCategories()) {
                is NetworkRequestResource.Error -> {
                    _courseListGetFlow.value =
                        CourseListGetEvent.Failure(courseCategories.message.toString())
                }

                is NetworkRequestResource.Success -> {
                    try {
                        _courseListGetFlow.value = CourseListGetEvent.Success(courseCategories.data!!)
                    }
                    catch (e: Exception){
                        _courseListGetFlow.value =
                            CourseListGetEvent.Failure(e.message.toString())
                    }
                    Log.d(TAG, courseCategories.toString())
                }
            }
        }
    }

    sealed class CourseGradeListGetEvent {
        class Success(val courseGradeList: List<CourseGradeListItem>) : CourseGradeListGetEvent()
        class Failure(val errorText: String) : CourseGradeListGetEvent()
        object Loading : CourseGradeListGetEvent()
        object Empty : CourseGradeListGetEvent()
    }

    private val _courseGradeListGetFlow = MutableStateFlow<CourseGradeListGetEvent>(CourseGradeListGetEvent.Empty)
    val courseGradeListGetFlow: MutableStateFlow<CourseGradeListGetEvent> = _courseGradeListGetFlow


    fun getCourseGrades(courseId : Int){
        // launch coroutine to request for course category list
        viewModelScope.launch(dispatchers.io) {

            // set the profile get event to loading state
            _courseGradeListGetFlow.value = CourseGradeListGetEvent.Loading

            when (val courseGradeList = repository.getCourseGrades(courseId)) {
                is NetworkRequestResource.Error -> {
                    _courseGradeListGetFlow.value = CourseGradeListGetEvent.Failure(courseGradeList.message.toString())
                }

                is NetworkRequestResource.Success -> {
                    try {
                        _courseGradeListGetFlow.value = CourseGradeListGetEvent.Success(courseGradeList.data!!)
                    }
                    catch (e: Exception){
                        _courseGradeListGetFlow.value = CourseGradeListGetEvent.Failure(e.message.toString())
                    }
                }
            }
        }
    }


}