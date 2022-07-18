package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.repository.AnswerRepository
import com.jrrobo.juniorrobo.utility.DataStorePreferencesManager
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityAnswerAQuestionViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val answerRepository: AnswerRepository,
    private val dataStorePreferencesManager: DataStorePreferencesManager
) : ViewModel() {

    private val TAG: String = javaClass.simpleName

    sealed class PostAnswerItemEvent {
        class Success(val resultText: String) : PostAnswerItemEvent()
        class Failure(val errorText: String) : PostAnswerItemEvent()
        object Loading : PostAnswerItemEvent()
        object Empty : PostAnswerItemEvent()
    }

    // function requesting the OTP taking the entered contact number as parameter
    fun postAnswer(
        answerItem: AnswerItem
    ) {

        // using the repository object request for the OTP
        viewModelScope.launch(dispatchers.io) {
            when (val response = answerRepository.postAnswer(answerItem)) {

                is NetworkRequestResource.Error -> {
                    Log.d(TAG, response.message.toString())
                }

                is NetworkRequestResource.Success -> {
                    Log.d(TAG, response.data.toString())
                }
            }
        }
    }

    fun getPkStudentIdPreference() = dataStorePreferencesManager.getPkStudentId().asLiveData()
}