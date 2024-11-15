package com.example.sleep_cycle.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.sleep_cycle.helpers.ErrorManager

@Composable
fun GlobalErrorHandler(errorManager: ErrorManager) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        errorManager.errorMessage.collect { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
