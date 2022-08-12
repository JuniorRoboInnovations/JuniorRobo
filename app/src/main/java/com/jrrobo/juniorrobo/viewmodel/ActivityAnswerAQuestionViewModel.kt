package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.data.answer.AnswerItemPost
import com.jrrobo.juniorrobo.data.answer.AnswerItemPostResponse
import com.jrrobo.juniorrobo.repository.AnswerRepository
import com.jrrobo.juniorrobo.utility.DataStorePreferencesManager
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.security.auth.login.LoginException

@HiltViewModel
class ActivityAnswerAQuestionViewModel @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val answerRepository: AnswerRepository,
    private val dataStorePreferencesManager: DataStorePreferencesManager
) : ViewModel() {

    private val TAG: String = javaClass.simpleName

    sealed class PostAnswerItemEvent {
        class Success(val answerItemPostResponse: AnswerItemPostResponse) : PostAnswerItemEvent()
        class Failure(val errorText: String) : PostAnswerItemEvent()
        object Loading : PostAnswerItemEvent()
        object Empty : PostAnswerItemEvent()
    }

    private val _postAnswerEventFlow = MutableStateFlow<PostAnswerItemEvent>(PostAnswerItemEvent.Empty)

    val postAnswerEventFlow: MutableStateFlow<PostAnswerItemEvent>
        get() = _postAnswerEventFlow

    // function requesting the OTP taking the entered contact number as parameter
    fun postAnswer(answerItemPost: AnswerItemPost) {

        viewModelScope.launch(dispatcher.io) {

            _postAnswerEventFlow.value = PostAnswerItemEvent.Loading

            when (val response = answerRepository.postAnswer(answerItemPost)) {

                is NetworkRequestResource.Error -> {
                    _postAnswerEventFlow.value =PostAnswerItemEvent.Failure(response.message!!)
                }

                is NetworkRequestResource.Success -> {

                    val parsedData= response.data
                    if(parsedData!=null){
                        _postAnswerEventFlow.value =PostAnswerItemEvent.Success(parsedData)
                    }
                    Log.d(TAG, "postQuestionItem: ${parsedData}")

                    Log.e(TAG,"${response.data}")
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
        viewModelScope.launch(dispatcher.io) {
            Log.d(TAG, "getAnswers: making api call from ActivityAnswerAQuestionViewModel")
            when (val response = answerRepository.getAnswer(q_id)) {

                is NetworkRequestResource.Success -> {
                    if (response.data != null) {
                        Log.d(TAG, response.data.toString())
                        _answers.postValue(response.data)
                    }
                }
                is NetworkRequestResource.Error -> {
                    Log.d(TAG, "getAnswers: Error->${response.message}")
                }
            }
        }
    }
    companion object {
        private const val DEFAULT_QUESTION_CATEGORY_ID = 1
    }
}