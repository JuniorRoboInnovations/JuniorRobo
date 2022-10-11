package com.jrrobo.juniorroboapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorroboapp.data.classroom.ClassroomDetails
import com.jrrobo.juniorroboapp.repository.ClassroomRepository
import com.jrrobo.juniorroboapp.utility.DataStorePreferencesManager
import com.jrrobo.juniorroboapp.utility.DispatcherProvider
import com.jrrobo.juniorroboapp.utility.NetworkRequestResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentClassroomViewModel @Inject constructor(
    private val repository: ClassroomRepository,
    private val dataStorePreferencesManager: DataStorePreferencesManager,
    private val dispatchers: DispatcherProvider
): ViewModel() {
    // TAG for logging purpose
    private val TAG: String = javaClass.simpleName

    sealed class ClassroomGetEvent {
        class Success(val classroomDetails: String) : ClassroomGetEvent()
        class Failure(val errorText: String) : ClassroomGetEvent()
        object Loading : ClassroomGetEvent()
        object Empty : ClassroomGetEvent()
    }

    private val _classroomCourses = MutableLiveData<List<ClassroomDetails>>()
    val classroomCourses: LiveData<List<ClassroomDetails>>
        get() = _classroomCourses

    fun getClassroomDetails(){

        viewModelScope.launch(dispatchers.io) {

            when (val classroomGetResponse = repository.classroomDetails()) {
                is NetworkRequestResource.Error -> {

                }
                is NetworkRequestResource.Success -> {
                    try {
                            if (classroomGetResponse.data != null){
                                _classroomCourses.postValue(classroomGetResponse.data!!)
                            }
                    }
                    catch (e: Exception){
                        Log.d(TAG, classroomGetResponse.toString())
                    }
                    Log.d(TAG, classroomGetResponse.toString())
                }

            }
        }
    }
}