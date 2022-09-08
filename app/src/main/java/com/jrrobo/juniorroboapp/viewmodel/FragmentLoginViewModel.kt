package com.jrrobo.juniorroboapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorroboapp.data.emailLogin.EmailRegisterData
import com.jrrobo.juniorroboapp.data.emailLogin.EmailRegisterResponse
import com.jrrobo.juniorroboapp.parser.OtpRequestParser
import com.jrrobo.juniorroboapp.repository.LoginOtpRepositoryLogin
import com.jrrobo.juniorroboapp.utility.DataStorePreferencesManager
import com.jrrobo.juniorroboapp.utility.DispatcherProvider
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
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

                    try {
                        // parse the requested body
                        val successResponse =
                            OtpRequestParser.otpRequest(otpResponse.data.toString())

                        if (successResponse.status == "Success") {
                            _otpRequestFlow.value = OtpEvent.Success(successResponse.message)
                        } else {
                            _otpRequestFlow.value = OtpEvent.Failure(successResponse.message)
                        }
                    }
                    catch (e : Exception){
                        _otpResponseFlow.value = OtpEvent.Failure(e.message.toString())
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

                    try {
                        Log.d(TAG, otpResponse.data.toString())

                        // parse the requested body
                        val successResponse =
                            OtpRequestParser.otpResponse(otpResponse.data.toString())

                        if (successResponse.status == "Success") {
                            _otpResponseFlow.value = OtpEvent.Success(successResponse.message)
                            Log.d(TAG, "responseOtp: ${successResponse.pkStudentId}")
                            dataStorePreferencesManager.setPkStudentId(successResponse.pkStudentId)

                            Log.d(TAG, successResponse.pkStudentId.toString())
                        } else {
                            _otpResponseFlow.value = OtpEvent.Failure(successResponse.message)
                        }
                    }
                    catch (e :Exception){
                        _otpResponseFlow.value = OtpEvent.Failure(e.message.toString())
                    }

                }
            }
        }
    }

    sealed class EmailEvent {
        class Success(val resultText: String) : EmailEvent()
        class Failure(val errorText: String) : EmailEvent()
        object Loading : EmailEvent()
        object Empty : EmailEvent()
    }

    private val _emailResponseFlow = MutableStateFlow<EmailEvent>(EmailEvent.Empty)

    val emailResponseFlow: MutableStateFlow<EmailEvent> = _emailResponseFlow

    fun responseEmail(
        email: String,
        password: String
    ){
        Log.e(TAG, "responseEmail: ", )
        viewModelScope.launch(dispatchers.io) {
            _emailResponseFlow.value = EmailEvent.Loading


            when (val emailResponse = repository.emailLogin(email, password)) {

                is NetworkRequestResource.Error -> {
                    _emailResponseFlow.value =
                        EmailEvent.Failure(emailResponse.message!!)
                }

                is NetworkRequestResource.Success -> {
                    try {
                        val successResponse =
                            OtpRequestParser.otpResponse(emailResponse.data.toString())

                        Log.e(TAG, "responseEmail: ${successResponse.status}", )
                        if (successResponse.status == "Success") {
                            _emailResponseFlow.value = EmailEvent.Success(successResponse.message)

                            dataStorePreferencesManager.setPkStudentId(successResponse.pkStudentId)

                            Log.d(TAG, successResponse.pkStudentId.toString())
                        } else {
                            _emailResponseFlow.value = EmailEvent.Failure(successResponse.message)
                        }
                    }
                    catch (e :Exception){
                        _emailResponseFlow.value = EmailEvent.Failure(e.message.toString())
                    }
                }
            }
        }
    }

    sealed class EmailRegisterEvent{
        class Success(val emailRegisterPostResponse: EmailRegisterResponse) : EmailRegisterEvent()
        class Failure(val errorText: String) : EmailRegisterEvent()
        object Loading : EmailRegisterEvent()
        object Empty : EmailRegisterEvent()
    }

    private val _emailRegisterEventFlow = MutableStateFlow<EmailRegisterEvent>(
        EmailRegisterEvent.Empty)

    val emailRegisterEventFlow: MutableStateFlow<EmailRegisterEvent>
        get() = _emailRegisterEventFlow

    fun registerEmail(emailRegister: EmailRegisterData){

        viewModelScope.launch(dispatchers.io) {
            _emailRegisterEventFlow.value = EmailRegisterEvent.Loading

            when(val response = repository.emailRegister(emailRegister)){
                is NetworkRequestResource.Error -> {
                    _emailRegisterEventFlow.value =
                        EmailRegisterEvent.Failure(response.message!!)
                }

                is NetworkRequestResource.Success -> {

                    try {
                        val parsedData = response.data
                        if (parsedData != null) {
                            _emailRegisterEventFlow.value = EmailRegisterEvent.Success(parsedData)
                        }
                    } catch (e: Exception) {
                        _emailRegisterEventFlow.value =
                            EmailRegisterEvent.Failure(e.message.toString())
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
    fun getOtpVerificationStatus() =
        dataStorePreferencesManager.getOtpVerificationStatus().asLiveData()

    // update the data store preference app launched
    fun setAppLaunchedStatus(appLaunched: Boolean) {
        viewModelScope.launch(dispatchers.io) {
            dataStorePreferencesManager.setAppLaunchedStatus(appLaunched)
        }
    }

    // get the data store preference of app launched status
    fun getAppLaunchedStatus() =
        dataStorePreferencesManager.getAppLaunchedStatus().asLiveData()

}