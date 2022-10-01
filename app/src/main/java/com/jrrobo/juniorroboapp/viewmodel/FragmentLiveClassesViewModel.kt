package com.jrrobo.juniorroboapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.jrrobo.juniorroboapp.data.booking.BookingDemoItem
import com.jrrobo.juniorroboapp.data.booking.BookingDemoItemPostResponse
import com.jrrobo.juniorroboapp.data.booking.BookingItem
import com.jrrobo.juniorroboapp.data.booking.BookingItemPostResponse
import com.jrrobo.juniorroboapp.data.course.CourseGradeDetail
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.data.course.CourseListItem
import com.jrrobo.juniorroboapp.data.profile.StudentProfileData
import com.jrrobo.juniorroboapp.data.voucher.Voucher
import com.jrrobo.juniorroboapp.repository.LiveClassesRepository
import com.jrrobo.juniorroboapp.utility.DataStorePreferencesManager
import com.jrrobo.juniorroboapp.utility.DispatcherProvider
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
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
        class Success(val bookingItemPostResponse: BookingItemPostResponse) : BookingItemPostEvent()
        class Failure(val errorText: String) : BookingItemPostEvent()
        object Loading : BookingItemPostEvent()
        object Empty : BookingItemPostEvent()
    }

    suspend fun postBookingItem(
        bookingItem: BookingItem
    ):BookingItemPostEvent {
        val bookingItemPostResponse = viewModelScope.async {
            repository.postBookingItem(bookingItem)
        }

        when (bookingItemPostResponse.await()) {

            // when the NetworkResource is Error then set the Profile update request event to
            // Error state with the error message
            is NetworkRequestResource.Error -> {
                return BookingItemPostEvent.Failure(bookingItemPostResponse.await().message!!)
            }

            // when the NetworkResource is Success set the BookingItemPost event to
            // Success state with the data got by the network resource
            is NetworkRequestResource.Success -> {
                return try {
                    BookingItemPostEvent.Success(bookingItemPostResponse.await().data!!)
                } catch (e : Exception){
                    BookingItemPostEvent.Failure(e.message.toString())
                }
            }
        }
    }


    /**
     * Booking demo post event
     * Request type: POST
     */
    sealed class BookingDemoItemPostEvent {
        class Success(val postBookingDemoItemPostResponse: BookingDemoItemPostResponse) : BookingDemoItemPostEvent()
        class Failure(val errorText: String) : BookingDemoItemPostEvent()
        object Loading : BookingDemoItemPostEvent()
        object Empty : BookingDemoItemPostEvent()
    }

    private val _bookingDemoItemPostFlow = MutableStateFlow<BookingDemoItemPostEvent>(BookingDemoItemPostEvent.Empty)
    val bookingDemoItemPostFlow: StateFlow<BookingDemoItemPostEvent> = _bookingDemoItemPostFlow

    fun postBookingDemoItem(
        bookingDemoItem: BookingDemoItem
    ) {
        // using the repository object injected launch the profile update event for POST request
        viewModelScope.launch(dispatchers.io) {

            // keep the event in the loading state
            _bookingDemoItemPostFlow.value = BookingDemoItemPostEvent.Loading

            // check the state of the NetworkResource data of the response after POST request
            when (val response = repository.postBookingDemoItem(bookingDemoItem)) {

                // when the NetworkResource is Error then set the Profile update request event to
                // Error state with the error message
                is NetworkRequestResource.Error -> {
                    _bookingDemoItemPostFlow.value =
                        BookingDemoItemPostEvent.Failure(response.message!!)
                }

                // when the NetworkResource is Success set the BookingItemPost event to
                // Success state with the data got by the network resource
                is NetworkRequestResource.Success -> {
                    try {
                        _bookingDemoItemPostFlow.value =
                            BookingDemoItemPostEvent.Success(response.data!!)
                    }
                    catch (e : Exception){
                        _bookingDemoItemPostFlow.value =
                            BookingDemoItemPostEvent.Failure(e.message.toString())
                    }
                    Log.d(TAG, response.data.toString())
                }
            }
        }
    }

    /**
     * Discount GET event
     * Request type: GET
     */
    sealed class DiscountGetEvent {
        class Success(val voucher: Voucher) : DiscountGetEvent()
        class Failure(val errorText: String) : DiscountGetEvent()
        object Loading : DiscountGetEvent()
        object Empty : DiscountGetEvent()
    }

    private val _discountGetFlow = MutableStateFlow<DiscountGetEvent>(DiscountGetEvent.Empty)
    val discountGetFlow: StateFlow<DiscountGetEvent> = _discountGetFlow

    fun getDiscount(
        couponCode: String
    ) {
        // using the repository object injected launch the profile update event for POST request
        viewModelScope.launch(dispatchers.io) {

            // keep the event in the loading state
            _discountGetFlow.value = DiscountGetEvent.Loading

            // check the state of the NetworkResource data of the response after POST request
            when (val response = repository.getDiscount(couponCode)) {

                // when the NetworkResource is Error then set the Profile update request event to
                // Error state with the error message
                is NetworkRequestResource.Error -> {
                    _discountGetFlow.value =
                        DiscountGetEvent.Failure(response.message!!)
                }

                // when the NetworkResource is Success set the BookingItemPost event to
                // Success state with the data got by the network resource
                is NetworkRequestResource.Success -> {
                    try {
                        _discountGetFlow.value =
                            DiscountGetEvent.Success(response.data!!)
                    }
                    catch (e : Exception){
                        _discountGetFlow.value =
                            DiscountGetEvent.Failure(e.message.toString())
                    }
                    Log.d(TAG, response.data.toString())
                }
            }
        }
    }

    /**
     * Hash GET event
     * Request type: GET
     */
    sealed class HashGetEvent {
        class Success(val sha512: String) : HashGetEvent()
        class Failure(val errorText: String) : HashGetEvent()
        object Loading : HashGetEvent()
        object Empty : HashGetEvent()
    }

    suspend fun getHash(
        hash: String
    ): HashGetEvent {
        val hashData = viewModelScope.async {
            repository.getHash(hash)
        }

        when (hashData.await()) {

            // when the NetworkResource is Error then set the Hash get event to
            // Error state with the error message
            is NetworkRequestResource.Error -> {
                return HashGetEvent.Failure(hashData.await().message!!)
            }

            // when the NetworkResource is Success set the get Hash event to
            // Success state with the data got by the network resource
            is NetworkRequestResource.Success -> {
                return try {
                    HashGetEvent.Success(hashData.await().data!!)
                } catch (e : Exception){
                    HashGetEvent.Failure(e.message.toString())
                }
            }
        }
    }

    /**
     * Profile get event
     * Request type: GET
     */
    sealed class ProfileGetEvent {
        class Success(val parsedStudentProfileData: StudentProfileData) : ProfileGetEvent()
        class Failure(val errorText: String) : ProfileGetEvent()
        object Loading : ProfileGetEvent()
        object Empty : ProfileGetEvent()
    }

    suspend fun getStudentProfile(
        pkStudentId: Int
    ): ProfileGetEvent {
        val studentProfileData = viewModelScope.async {
            repository.getStudentProfile(pkStudentId)
        }

        when (studentProfileData.await()) {

            // when the NetworkResource is Error then set the Profile update request event to
            // Error state with the error message
            is NetworkRequestResource.Error -> {
                return ProfileGetEvent.Failure(studentProfileData.await().message!!)
            }

            // when the NetworkResource is Success set the BookingItemPost event to
            // Success state with the data got by the network resource
            is NetworkRequestResource.Success -> {
                return try {
                    ProfileGetEvent.Success(studentProfileData.await().data!!)
                } catch (e : Exception){
                    ProfileGetEvent.Failure(e.message.toString())
                }
            }
        }
    }


    suspend fun getPkStudentIdPreference() = dataStorePreferencesManager.getPrimaryKeyStudentId()


}