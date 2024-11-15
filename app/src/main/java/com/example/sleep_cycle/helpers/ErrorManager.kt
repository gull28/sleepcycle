package com.example.sleep_cycle.helpers

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorManager @Inject constructor() {
    private val _errorMessage = MutableSharedFlow<String?>();
    val errorMessage: SharedFlow<String?> = _errorMessage

    suspend fun postError(message: String) {
        _errorMessage.emit(message)
    }
}