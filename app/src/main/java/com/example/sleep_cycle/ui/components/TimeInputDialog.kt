import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sleep_cycle.data.model.SleepTime
import java.util.*

@Composable
fun TimeInputDialog(
    sleepTime: SleepTime?,
    setShowDialog: (value: Boolean) -> Unit,
    onSave: (SleepTime) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(TextFieldValue(sleepTime?.name ?: "")) }
    var startTime by remember { mutableStateOf(sleepTime?.startTime ?: "00:00") }
    var duration by remember { mutableStateOf(sleepTime?.duration ?: 0) }

    fun openStartTimePicker() {
        val calendar = Calendar.getInstance()
        val initialHour = startTime.split(":")[0].toIntOrNull() ?: calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = startTime.split(":")[1].toIntOrNull() ?: calendar.get(Calendar.MINUTE)

        TimePickerDialog(context, { _, hourOfDay, minute ->
            startTime = String.format("%02d:%02d", hourOfDay, minute)
        }, initialHour, initialMinute, true).show()
    }

    fun openDurationPicker() {
        val initialHour = duration / 60
        val initialMinute = duration % 60

        TimePickerDialog(context, { _, hourOfDay, minute ->
            duration = hourOfDay * 60 + minute
        }, initialHour, initialMinute, true).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Sleep Time") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { openStartTimePicker() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Text("Start Time: $startTime")
                }

                Button(
                    onClick = { openDurationPicker() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val durationHours = duration / 60
                    val durationMinutes = duration % 60
                    Text("Duration: ${String.format("%02d:%02d", durationHours, durationMinutes)}")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.text.isNotBlank() && duration > 0 && duration < 1440) {
                    val sleepTime = SleepTime(
                        id = sleepTime?.id,
                        scheduleId = sleepTime?.scheduleId,
                        name = name.text,
                        startTime = startTime,
                        duration = duration
                    )
                    onSave(sleepTime)
                    onDismiss()
                } else {
                    Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
