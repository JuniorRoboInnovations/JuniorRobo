package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemToAsk
import com.jrrobo.juniorrobo.repository.QuestionRepository
import com.jrrobo.juniorrobo.utility.DataStorePreferencesManager
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityAskQuestionActivityViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val dispatcher: DispatcherProvider,
    private val dataStorePreferencesManager: DataStorePreferencesManager
) : ViewModel() {

    private val TAG: String = javaClass.simpleName

    sealed class PostQuestionItemEvent {
        class Success(val resultText: String) : PostQuestionItemEvent()
        class Failure(val errorText: String) : PostQuestionItemEvent()
        object Loading : PostQuestionItemEvent()
        object Empty : PostQuestionItemEvent()
    }

    private val _postQuestionEventFlow =
        MutableStateFlow<PostQuestionItemEvent>(PostQuestionItemEvent.Empty)

    val postQuestionEventFlow: MutableStateFlow<PostQuestionItemEvent>
        get() = _postQuestionEventFlow

    fun postQuestionItem(questionItemToAsk: QuestionItemToAsk) {
        viewModelScope.launch(dispatcher.io) {

            _postQuestionEventFlow.value = PostQuestionItemEvent.Loading

            when (val response = questionRepository.postQuestionItem(questionItemToAsk)) {

                is NetworkRequestResource.Error -> {
                    _postQuestionEventFlow.value =
                        PostQuestionItemEvent.Failure(response.message!!)
                    Log.d(TAG, response.toString())
                }

                is NetworkRequestResource.Success -> {

                    // parse the requested body
                    val successResponse = response.data

                    Log.d(TAG, successResponse.toString())

                    if (successResponse!!.success) {
                        _postQuestionEventFlow.value =
                            PostQuestionItemEvent.Success(successResponse.message)
                    } else {
                        _postQuestionEventFlow.value =
                            PostQuestionItemEvent.Failure(successResponse.message)
                    }
                }
            }
        }
    }

    fun getPkStudentIdPreference() = dataStorePreferencesManager.getPkStudentId().asLiveData()
}