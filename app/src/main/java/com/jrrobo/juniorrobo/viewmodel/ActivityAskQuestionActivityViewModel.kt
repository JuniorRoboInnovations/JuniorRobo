package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemPostResponse
import com.jrrobo.juniorrobo.data.questionitem.QuestionItemToAsk
import com.jrrobo.juniorrobo.repository.QuestionRepository
import com.jrrobo.juniorrobo.utility.DataStorePreferencesManager
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ActivityAskQuestionActivityViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val dispatcher: DispatcherProvider,
    private val dataStorePreferencesManager: DataStorePreferencesManager
) : ViewModel() {

    private val TAG: String = javaClass.simpleName

    sealed class PostQuestionItemEvent {
        class Success(val questionItemPostResponse: QuestionItemPostResponse) : PostQuestionItemEvent()
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
                }

                is NetworkRequestResource.Success -> {

                    val parsedData= response.data
                    if(parsedData!=null){
                        _postQuestionEventFlow.value =
                            PostQuestionItemEvent.Success(parsedData)
                    }
                    Log.d(TAG, "postQuestionItem: $parsedData")
                }
            }
        }
    }

    private val _questionCategoriesLiveData = MutableLiveData<List<QuestionCategoryItem>>()

    val questionCategoriesLiveData: LiveData<List<QuestionCategoryItem>>
        get() = _questionCategoriesLiveData

    fun getQuestionCategories() {
        viewModelScope.launch(dispatcher.io) {

            when (val response = questionRepository.getQuestionCategories()) {
                is NetworkRequestResource.Success -> {
                    if (response.data != null) {
                        Log.d(TAG, response.data.toString())
                        _questionCategoriesLiveData.postValue(response.data)
                    }
                }
                is NetworkRequestResource.Error -> {
                    Log.d(TAG, "getQuestionCategories: Error->${response.message}")
                }
            }
        }
    }

    sealed class PostQuestionImageEvent {
        class Success(val questionImagePostResponse: String) : PostQuestionImageEvent()
        class Failure(val errorText: String) : PostQuestionImageEvent()
        object Loading : PostQuestionImageEvent()
        object Empty : PostQuestionImageEvent()
    }

    private val _postQuestionImageEventFlow =
        MutableStateFlow<PostQuestionImageEvent>(PostQuestionImageEvent.Empty)

    val postQuestionImageEventFlow: MutableStateFlow<PostQuestionImageEvent>
        get() = _postQuestionImageEventFlow

    fun postQuestionImage(questionImage: File) {
        viewModelScope.launch(dispatcher.io) {

            _postQuestionImageEventFlow.value = PostQuestionImageEvent.Loading

            when (val response = questionRepository.postQuestionImage(questionImage)) {

                is NetworkRequestResource.Error -> {
                    _postQuestionImageEventFlow.value =
                        PostQuestionImageEvent.Failure(response.message!!)
                }

                is NetworkRequestResource.Success -> {

                    val parsedData= response.data
                    if(parsedData!=null){
                        _postQuestionImageEventFlow.value =
                            PostQuestionImageEvent.Success(parsedData)
                    }
                    Log.d(TAG, "postQuestionItem: $parsedData")
                }
            }
        }
    }



    fun getPkStudentIdPreference() = dataStorePreferencesManager.getPkStudentId().asLiveData()
}