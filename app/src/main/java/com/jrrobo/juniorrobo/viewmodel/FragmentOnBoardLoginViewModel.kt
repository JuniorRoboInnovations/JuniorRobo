package com.jrrobo.juniorrobo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jrrobo.juniorrobo.utility.DataStorePreferencesManager
import com.jrrobo.juniorrobo.utility.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentOnBoardLoginViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val dataStorePreferencesManager: DataStorePreferencesManager,
) : ViewModel() {

    // set the onboarding status to true or false if the user has completed the onboarding or not
    // taking the status parameter of boolean
    fun setOnBoardStatus(onBoardStatusBoolean: Boolean) {
        viewModelScope.launch(dispatchers.io) {
            dataStorePreferencesManager.setOnBoardStatus(onBoardStatusBoolean)
        }
    }

    // get the onboarding status of the user as live data
    fun getOnBoardStatus() = dataStorePreferencesManager.getOnBoardingStatus().asLiveData()
}

