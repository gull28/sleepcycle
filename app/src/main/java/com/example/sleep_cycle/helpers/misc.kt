package com.example.sleep_cycle.helpers

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.sleep_cycle.data.models.SleepTime

data class ValidationResult(
    val isValid: Boolean,
    val message: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
fun canAddSleepTime(sleepTimes: MutableList<SleepTime>, sleepTimeToAdd: SleepTime): ValidationResult {
    val filteredSleepTimes = sleepTimes.filter {
        it.id != sleepTimeToAdd.id;
    }

    val overlappingTime = filteredSleepTimes.find {
        it.isTimeInTimeFrame(sleepTimeToAdd.startTime, sleepTimeToAdd.calculateEndTime())
    }
    val sameStartTime = filteredSleepTimes.find { it.startTime == sleepTimeToAdd.startTime }

    return if (overlappingTime != null) {
        ValidationResult(isValid = false, message = "This time overlaps with an existing SleepTime.")
    } else if (sameStartTime == null) {

        ValidationResult(isValid = true)
    } else {
        ValidationResult(isValid = false, message = "A SleepTime with the same start time already exists.")
    }
}

fun canUpdateSleepTime(sleepTimes: List<SleepTime>) {

}