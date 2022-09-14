package com.jrrobo.juniorroboapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorroboapp.data.booking.BookingItem
import com.jrrobo.juniorroboapp.data.course.CourseGradeDetail
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
        // launch coroutine to request for course grade list
        viewModelScope.launch(dispatchers.io) {

            // set the course grade get event to loading state
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

    sealed class CourseGradeDetailsGetEvent {
        class Success(val courseGradeDetail: CourseGradeDetail) : CourseGradeDetailsGetEvent()
        class Failure(val errorText: String) : CourseGradeDetailsGetEvent()
        object Loading : CourseGradeDetailsGetEvent()
        object Empty : CourseGradeDetailsGetEvent()
    }

    private val _courseGradeDetailsGetFlow = MutableStateFlow<CourseGradeDetailsGetEvent>(CourseGradeDetailsGetEvent.Empty)
    val courseGradeDetailsGetFlow: MutableStateFlow<CourseGradeDetailsGetEvent> = _courseGradeDetailsGetFlow


    fun getCourseGradeDetails(id : Int){
        // launch coroutine to request for course details
        viewModelScope.launch(dispatchers.io) {

            // set the profile get event to loading state
            _courseGradeDetailsGetFlow.value = CourseGradeDetailsGetEvent.Loading

            when (val courseGradeDetail = repository.getCourseGradeDetails(id)) {
                is NetworkRequestResource.Error -> {
                    _courseGradeDetailsGetFlow.value = CourseGradeDetailsGetEvent.Failure(courseGradeDetail.message.toString())
                }

                is NetworkRequestResource.Success -> {
                    try {
                        _courseGradeDetailsGetFlow.value = CourseGradeDetailsGetEvent.Success(courseGradeDetail.data!!)
                    }
                    catch (e: Exception){
                        _courseGradeDetailsGetFlow.value = CourseGradeDetailsGetEvent.Failure(e.message.toString())
                    }
                }
            }
        }
    }

    /**
     * Booking post event
     * Request type: POST
     */
    sealed class BookingItemPostEvent {
        class Success(val pk_id: Int) : BookingItemPostEvent()
        class Failure(val errorText: String) : BookingItemPostEvent()
        object Loading : BookingItemPostEvent()
        object Empty : BookingItemPostEvent()
    }

    private val _bookingPostFlow = MutableStateFlow<BookingItemPostEvent>(BookingItemPostEvent.Empty)
    val bookingPostFlow: MutableStateFlow<BookingItemPostEvent> = _bookingPostFlow

    fun postBookingItem(
        bookingItem: BookingItem
    ) {
        // using the repository object injected launch the profile update event for POST request
        viewModelScope.launch(dispatchers.io) {

            // keep the event in the loading state
            _bookingPostFlow.value = BookingItemPostEvent.Loading

            // check the state of the NetworkResource data of the response after POST request
            when (val updateProfileResponse = repository.postBookingItem(bookingItem)) {

                // when the NetworkResource is Error then set the Profile update request event to
                // Error state with the error message
                is NetworkRequestResource.Error -> {
                    _bookingPostFlow.value =
                        BookingItemPostEvent.Failure(updateProfileResponse.message!!)
                }

                // when the NetworkResource is Success set the BookingItemPost event to
                // Success state with the data got by the network resource
                is NetworkRequestResource.Success -> {
                    try {
                        _bookingPostFlow.value =
                            BookingItemPostEvent.Success(updateProfileResponse.data!!)
                    }
                    catch (e : Exception){
                        _bookingPostFlow.value =
                            BookingItemPostEvent.Failure(e.message.toString())
                    }
                    Log.d(TAG, updateProfileResponse.data.toString())
                }
            }
        }
    }

    fun getPkStudentIdPreference() = dataStorePreferencesManager.getPkStudentId().asLiveData()


}