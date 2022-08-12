package com.jrrobo.juniorrobo.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategory
import com.jrrobo.juniorrobo.data.questioncategory.QuestionCategoryItem
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.network.JuniorRoboApi
import com.jrrobo.juniorrobo.repository.QuestionRepository
import com.jrrobo.juniorrobo.utility.DataStorePreferencesManager
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import com.jrrobo.juniorrobo.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentQuestionsViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val dispatchers: DispatcherProvider,
    private val dataStorePreferencesManager: DataStorePreferencesManager,
    private val juniorRoboApi: JuniorRoboApi
) : ViewModel() {

    private val TAG: String = javaClass.simpleName

    private val currentQuestionCategoryId = MutableLiveData(DEFAULT_QUESTION_CATEGORY_ID)

    val questions = currentQuestionCategoryId.switchMap { questionCategoryId ->
        questionRepository.getAllQuestionList(questionCategoryId, 0).cachedIn(viewModelScope)
    }

    // paging data----
    private val _questionsWithoutPaging = MutableLiveData<List<QuestionItem>>()
    val questionsWithoutPaging : LiveData<List<QuestionItem>>
        get() = _questionsWithoutPaging

    fun getQuestionsWithoutPaging(cat_id: Int?,keyword:String?,u_id:Int?){
        viewModelScope.launch(dispatchers.io) {
            when (val response = questionRepository.getAllQuestionsWithoutPaging(cat_id,keyword,u_id)) {
                is NetworkRequestResource.Success -> {
                    try {
                        if (response.data != null) {
                            var parsedQuestions = response.data
                            // filtering of data, currently 1 is for all the questions that are posted
                            // TODO : need to be optimized
                            if (cat_id != null && cat_id != 1 && cat_id != 13) {
                                parsedQuestions = parsedQuestions.filter { questionItem ->
                                    questionItem.question_sub_text == questionCategoriesLiveData.value?.find { questionCategoryItem ->
                                        questionCategoryItem.pkCategoryId == cat_id
                                    }?.categoryTitle
                                }
                            }
                            _questionsWithoutPaging.postValue(parsedQuestions)
                        }
                    }
                    catch (e : Exception){
                        Log.d(TAG, "getQuestions: Error->${e.message}")
                    }
                }
                is NetworkRequestResource.Error -> {
                    Log.d(TAG, "getQuestions: Error->${response.message}")
                }
            }
        }
    }
    // paging data----

    fun getQuestions(cat_id: Int) {
        currentQuestionCategoryId.value = cat_id
    }

    private val _questionCategoriesLiveData = MutableLiveData<List<QuestionCategoryItem>>()

    val questionCategoriesLiveData: LiveData<List<QuestionCategoryItem>>
        get() = _questionCategoriesLiveData

    fun getQuestionCategories() {
        viewModelScope.launch(dispatchers.io) {
            Log.d(TAG, "getQuestionCategories: making api call from FragmentQuestionsViewModel")

            when (val response = questionRepository.getQuestionCategories()) {
                is NetworkRequestResource.Success -> {
                    try {
                        if (response.data != null) {
                            Log.d(TAG, response.data.toString())
                            _questionCategoriesLiveData.postValue(response.data)
                        }
                    }
                    catch (e : Exception){
                        Log.d(TAG, "getQuestionCategories: Error->${e.message}")
                    }
                }
                is NetworkRequestResource.Error -> {
                    Log.d(TAG, "getQuestionCategories: Error->${response.message}")
                }
            }
        }
    }
    fun getPkStudentIdPreference() = dataStorePreferencesManager.getPkStudentId().asLiveData()

    companion object {
        private const val DEFAULT_QUESTION_CATEGORY_ID = 1
    }
}