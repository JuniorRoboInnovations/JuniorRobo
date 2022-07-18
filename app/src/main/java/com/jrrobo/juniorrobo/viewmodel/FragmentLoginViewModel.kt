package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorrobo.parser.OtpRequestParser
import com.jrrobo.juniorrobo.repository.LoginOtpRepositoryLogin
import com.jrrobo.juniorrobo.utility.DataStorePreferencesManager
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentLoginViewModel @Inject constructor(
    private val repository: LoginOtpRepositoryLogin,
    private val dispatchers: DispatcherProvider,
    private val dataStorePreferencesManager: DataStorePreferencesManager,
) : ViewModel() {

    // TAG of classname for logging or debugging purpose
    private val TAG: String = javaClass.simpleName

    // sealed class for Event handling
    sealed class OtpEvent {
        class Success(val resultText: String) : OtpEvent()
        class Failure(val errorText: String) : OtpEvent()
        object Loading : OtpEvent()
        object Empty : OtpEvent()
    }

    // private mutable otpRequest flow for emitting the OTP request data
    private val _otpRequestFlow = MutableStateFlow<OtpEvent>(OtpEvent.Empty)

    // public mutable otpRequest flow for emitting the data of the request when observed
    val otpFlow: MutableStateFlow<OtpEvent> = _otpRequestFlow

    // function requesting the OTP taking the entered contact number as parameter
    fun requestOtp(
        contactNumber: String,
    ) {

        // using the repository object request for the OTP
        viewModelScope.launch(dispatchers.io) {

            // keep the event in the loading state
            _otpRequestFlow.value = OtpEvent.Loading

            // check the state of the NetworkResource data of the response after requesting the OTP
            when (val otpResponse = repository.requestOtp(contactNumber)) {

                // when the NetworkResource is Error then set the OTP request event to
                // Error state with the error message
                is NetworkRequestResource.Error -> _otpRequestFlow.value =
                    OtpEvent.Failure(otpResponse.message!!)

                // when the NetworkResource is Success set the OTP request event to
                // Success state with the data got by the network resource
                is NetworkRequestResource.Success -> {

                    // parse the requested body
                    val successResponse =
                        OtpRequestParser.otpRequest(otpResponse.data.toString())

                    if (successResponse.success) {
                        _otpRequestFlow.value = OtpEvent.Success(successResponse.message)
                    } else {
                        _otpRequestFlow.value = OtpEvent.Failure(successResponse.message)
                    }
                }
            }
        }
    }

    // private mutable otpResponse flow for emitting the OTP response data
    private val _otpResponseFlow = MutableStateFlow<OtpEvent>(OtpEvent.Empty)

    // public mutable otpResponse flow for emitting the data of the request when observed
    val otpResponseFlow: MutableStateFlow<OtpEvent> = _otpResponseFlow

    // function requesting the OTP taking the entered contact number as parameter
    fun responseOtp(
        contactNumber: String,
        userOtp: String,
    ) {

        // using the repository object get the response for the OTP
        viewModelScope.launch(dispatchers.io) {

            // keep the event in the loading state
            _otpResponseFlow.value = OtpEvent.Loading

            // check the state of the NetworkResource data of the response
            // after getting response of the OTP
            when (val otpResponse = repository.responseOtp(contactNumber, userOtp)) {

                // when the NetworkResource is Error then set the OTP response event to
                // Error state with the error message
                is NetworkRequestResource.Error -> {
                    _otpResponseFlow.value =
                        OtpEvent.Failure(otpResponse.message!!)
                }

                // when the NetworkResource is Success set the OTP request event to
                // Success state with the data got by the network resource
                is NetworkRequestResource.Success -> {

                    Log.d(TAG, otpResponse.data.toString())

                    // parse the requested body
                    val successResponse =
                        OtpRequestParser.otpResponse(otpResponse.data.toString())

                    if (successResponse.status == "Success") {
                        _otpResponseFlow.value = OtpEvent.Success(successResponse.message)

                        dataStorePreferencesManager.setPkStudentId(successResponse.pkStudentId)

                        Log.d(TAG, successResponse.pkStudentId.toString())
                    } else {
                        _otpResponseFlow.value = OtpEvent.Failure(successResponse.message)
                    }
                }
            }
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