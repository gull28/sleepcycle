package com.example.sleep_cycle.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleep_cycle.data.repository.Preference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val preference: Preference
) : ViewModel() {

    val modeFlow: Flow<Boolean> = preference.modeFlow

     fun saveMode(value: Boolean){
         viewModelScope.launch {
             preference.saveMode(value)
         }
    }
}
