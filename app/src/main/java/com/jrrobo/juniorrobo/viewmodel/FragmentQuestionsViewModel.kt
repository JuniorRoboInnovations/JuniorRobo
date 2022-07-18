package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategory
import com.jrrobo.juniorrobo.network.JuniorRoboApi
import com.jrrobo.juniorrobo.repository.QuestionRepository
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentQuestionsViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val dispatchers: DispatcherProvider,
    private val juniorRoboApi: JuniorRoboApi
) : ViewModel() {

    private val TAG: String = javaClass.simpleName

    private val currentQuestionCategoryId = MutableLiveData(DEFAULT_QUESTION_CATEGORY_ID)

    val questions = currentQuestionCategoryId.switchMap { questionCategoryId ->
        questionRepository.getAllQuestionList(questionCategoryId, 0).cachedIn(viewModelScope)
    }

    fun getQuestions(cat_id: Int) {
        currentQuestionCategoryId.value = cat_id
    }

    private val _questionCategoriesLiveData = MutableLiveData<QuestionCategory>()

    val questionCategoriesLiveData: LiveData<QuestionCategory>
        get() = _questionCategoriesLiveData

    fun getQuestionCategories() {
        viewModelScope.launch(dispatchers.io) {
            when (val response = questionRepository.getQuestionCategories()) {
                is NetworkRequestResource.Success -> {
                    if (response.data != null) {
                        Log.d(TAG, response.data.toString())
                        _questionCategoriesLiveData.postValue(response.data!!)
                    }
                }
                is NetworkRequestResource.Error -> {

                }
            }
        }
    }

    companion object {
        private const val DEFAULT_QUESTION_CATEGORY_ID = 1
    }
}