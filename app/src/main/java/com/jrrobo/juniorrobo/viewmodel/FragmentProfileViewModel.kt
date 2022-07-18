package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.parser.ProfileUpdateRequestsParser
import com.jrrobo.juniorrobo.repository.ProfileRepository
import com.jrrobo.juniorrobo.utility.DataStorePreferencesManager
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val dataStorePreferencesManager: DataStorePreferencesManager,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {

    // TAG for logging purpose
    private val TAG: String = javaClass.simpleName

    /**
     * Profile update event
     * Request type: POST
     */
    sealed class ProfileUpdateEvent {
        class Success(val parsedStudentProfileData: StudentProfileData) : ProfileUpdateEvent()
        class Failure(val errorText: String) : ProfileUpdateEvent()
        object Loading : ProfileUpdateEvent()
        object Empty : ProfileUpdateEvent()
    }

    private val _profileUpdateFlow = MutableStateFlow<ProfileUpdateEvent>(ProfileUpdateEvent.Empty)
    val profileUpdateFlow: MutableStateFlow<ProfileUpdateEvent> = _profileUpdateFlow

    fun updateProfile(
        studentProfileData: StudentProfileData,
    ) {

        // using the repository object injected launch the profile update event for POST request
        viewModelScope.launch(dispatchers.io) {

            // keep the event in the loading state
            _profileUpdateFlow.value = ProfileUpdateEvent.Loading

            // check the state of the NetworkResource data of the response after POST request
            when (val updateProfileResponse = repository.updateProfile(studentProfileData)) {

                // when the NetworkResource is Error then set the Profile update request event to
                // Error state with the error message
                is NetworkRequestResource.Error -> {
                    _profileUpdateFlow.value =
                        ProfileUpdateEvent.Failure(updateProfileResponse.message!!)
                }

                // when the NetworkResource is Success set the Profile update request event to
                // Success state with the data got by the network resource
                is NetworkRequestResource.Success -> {

                    // parse the response of the profile update POST request
                    val parsedStudentProfileResponse: StudentProfileData =
                        ProfileUpdateRequestsParser.parseStudentProfileUpdateResponse(
                            updateProfileResponse.data.toString()
                        )

                    _profileUpdateFlow.value =
                        ProfileUpdateEvent.Success(parsedStudentProfileResponse)

                    Log.d(TAG, parsedStudentProfileResponse.toString())
                }
            }
        }
    }

    fun getPkStudentIdPreference() = dataStorePreferencesManager.getPkStudentId().asLiveData()

    fun setProfileCreatedStatus(profileCreatedStatus: Boolean) {
        viewModelScope.launch(dispatchers.io) {
            dataStorePreferencesManager.setProfileCreatedStatus(profileCreatedStatus)
        }
    }

    fun getProfileCreatedStatus() =
        dataStorePreferencesManager.getProfileCreatedStatus().asLiveData()

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

    private val _profileGetFlow = MutableStateFlow<ProfileGetEvent>(ProfileGetEvent.Empty)
    val profileGetFlow: MutableStateFlow<ProfileGetEvent> = _profileGetFlow

    fun getStudentProfile(
        pkStudentId: Int,
    ) {

        // launch coroutine to request for the profile data
        viewModelScope.launch(dispatchers.io) {

            // set the profile get event to loading state
            _profileGetFlow.value = ProfileGetEvent.Loading

            when (val profileGetResponse = repository.getStudentProfile(pkStudentId)) {
                is NetworkRequestResource.Error -> {
                    _profileGetFlow.value =
                        ProfileGetEvent.Failure(profileGetResponse.message.toString())
                }

                is NetworkRequestResource.Success -> {
                    val studentProfileGetData: StudentProfileData =
                        ProfileUpdateRequestsParser.parseStudentProfileGetResponse(
                            profileGetResponse.data.toString()
                        )

                    _profileGetFlow.value = ProfileGetEvent.Success(studentProfileGetData)

                    Log.d(TAG, studentProfileGetData.toString())
                }
            }
        }
    }
}