package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
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

    //Answer Image getter to be implemented
    //Answer Request GET to be implemented

    sealed class GetAnswerItemEvent {
        class Success(val resultText: String) : GetAnswerItemEvent()
        class Failure(val errorText: String) : GetAnswerItemEvent()
        object Loading : GetAnswerItemEvent()
        object Empty : GetAnswerItemEvent()
    }
    private val currentQuestionCategoryId = MutableLiveData(ActivityAnswerAQuestionViewModel.DEFAULT_QUESTION_CATEGORY_ID)

    private val _answers = MutableLiveData<List<AnswerItem>>()
    val answers : LiveData<List<AnswerItem>>
        get() = _answers

    fun getAnswer(
        q_id: Int
    ){
        viewModelScope.launch(dispatchers.io) {
            Log.d(TAG, "getAnswers: making api call from ActivityAnswerAQuestionViewModel")
            when (val response = answerRepository.getAnswer(q_id)) {
                is NetworkRequestResource.Success -> {
                    if (response.data != null) {
                        Log.d(TAG, response.data.toString())
                        _answers.postValue(response.data)
                    }
                }
                is NetworkRequestResource.Error -> {
                    Log.d(TAG, "getQuestions: Error->${response.message}")
                }
            }
        }
    }
    companion object {
        private const val DEFAULT_QUESTION_CATEGORY_ID = 1
    }
}