package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import android.widget.ExpandableListView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.repository.ProfileRepository
import com.jrrobo.juniorrobo.utility.DataStorePreferencesManager
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
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
        studentProfileData: StudentProfileData
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
                    try {
                        _profileUpdateFlow.value =
                            ProfileUpdateEvent.Success(updateProfileResponse.data!!)
                    }
                    catch (e : Exception){
                        _profileUpdateFlow.value =
                            ProfileUpdateEvent.Failure(e.message.toString())
                    }
                    Log.d(TAG, updateProfileResponse.data.toString())
                }
            }
        }
    }

    /**
    * Image upload event
    * Request type: POST
    */

    sealed class ImageUploadEvent {
        class Success(val hashedImageName: String) : ImageUploadEvent()
        class Failure(val errorText: String) : ImageUploadEvent()
        object Loading : ImageUploadEvent()
        object Empty : ImageUploadEvent()
    }

    private val _imageUploadFlow = MutableStateFlow<ImageUploadEvent>(ImageUploadEvent.Empty)
    val imageUploadFlow: MutableStateFlow<ImageUploadEvent> = _imageUploadFlow


    fun uploadProfileImage(profilePictureFile: File?) {
        if (profilePictureFile!=null){
            viewModelScope.launch {
                // keep the event in the loading state
                _imageUploadFlow.value = ImageUploadEvent.Loading

                // check the state of the NetworkResource data of the response after POST request
                when (val uploadImageResponse = repository.uploadImage(profilePictureFile)) {

                    // when the NetworkResource is Error then set the Profile update request event to
                    // Error state with the error message
                    is NetworkRequestResource.Error -> {
                        _imageUploadFlow.value =
                            ImageUploadEvent.Failure(uploadImageResponse.message!!)
                    }

                    // when the NetworkResource is Success set the Profile update request event to
                    // Success state with the data got by the network resource
                    is NetworkRequestResource.Success -> {

                        // parse the response of the image upload POST request
                        try {
                            _imageUploadFlow.value =
                                ImageUploadEvent.Success(uploadImageResponse.data.toString())
                        }
                        catch (e :Exception){
                            _imageUploadFlow.value =
                                ImageUploadEvent.Failure(e.message.toString())
                        }
                        Log.d(TAG, uploadImageResponse.data.toString())
                    }
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
                    try {
                        _profileGetFlow.value = ProfileGetEvent.Success(profileGetResponse.data!!)
                    }
                    catch (e: Exception){
                        _profileGetFlow.value =
                            ProfileGetEvent.Failure(e.message.toString())
                    }
                    Log.d(TAG, profileGetResponse.toString())
                }
            }
        }
    }




    // get the onboarding status of the user as live data
    fun getOnBoardStatus() = dataStorePreferencesManager.getOnBoardingStatus().asLiveData()


    // set the onboarding status to true or false if the user has completed the onboarding or not
    // taking the status parameter of boolean
    fun setOnBoardStatus(onBoardStatusBoolean: Boolean) {
        viewModelScope.launch(dispatchers.io) {
            dataStorePreferencesManager.setOnBoardStatus(onBoardStatusBoolean)
        }
    }

    // update the data store preference of the OTP verification status
    fun setOtpVerificationStatus(otpVerificationStatusBoolean: Boolean) {
        viewModelScope.launch(dispatchers.io) {
            dataStorePreferencesManager.setOtpVerificationStatus(otpVerificationStatusBoolean)
        }
    }

    // get the data store preference of the OTP verification status to directly navigate to the
    // Home Screen after reopening the app
    fun getOtpVerificationStatus() =
        dataStorePreferencesManager.getOtpVerificationStatus().asLiveData()
}