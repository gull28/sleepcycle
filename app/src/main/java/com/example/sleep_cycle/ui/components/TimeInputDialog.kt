import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.ui.theme.AppColors
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
    var startTime by remember { mutableStateOf(sleepTime?.startTime ?: "00:00:00") }
    var duration by remember { mutableStateOf(sleepTime?.duration ?: 0) }

    fun openStartTimePicker() {
        val calendar = Calendar.getInstance()
        val initialHour = startTime.split(":")[0].toIntOrNull() ?: calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = startTime.split(":")[1].toIntOrNull() ?: calendar.get(Calendar.MINUTE)

        TimePickerDialog(context, { _, hourOfDay, minute ->
            // Append :00 for seconds
            startTime = String.format("%02d:%02d:00", hourOfDay, minute)
        }, initialHour, initialMinute, true).show()
    }

    fun openDurationPicker() {
        val initialHour = duration / 60
        val initialMinute = duration % 60

        TimePickerDialog(context, { _, hourOfDay, minute ->
            // Convert duration to minutes and seconds (0 seconds by default)
            duration = hourOfDay * 60 + minute
        }, initialHour, initialMinute, true).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.DialogBackGround,
        title = {
            Text(
                "Add Sleep Time",
                style = MaterialTheme.typography.titleLarge,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = AppColors.Slate.copy(alpha = 0.75f),
                        focusedBorderColor = AppColors.Primary,
                        focusedLabelColor = AppColors.TextPrimary,
                        unfocusedTextColor = AppColors.TextPrimary,
                        unfocusedLabelColor = AppColors.TextPrimary.copy(alpha = 0.75f),
                        focusedTextColor = AppColors.TextPrimary,
                    )
                )

                Button(
                    onClick = { openStartTimePicker() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary
                    ),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Start Time: ${startTime.substring(0, 5)}", style = MaterialTheme.typography.bodyLarge)
                }

                Button(
                    onClick = { openDurationPicker() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary
                    ),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    val durationHours = duration / 60
                    val durationMinutes = duration % 60
                    Text(
                        "Duration: ${String.format("%02d:%02d", durationHours, durationMinutes)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.text.isNotBlank() && duration > 0 && duration < 1440) {
                        val sleep = SleepTime(
                            id = sleepTime?.id,
                            scheduleId = sleepTime?.scheduleId,
                            name = name.text,
                            startTime = startTime,
                            duration = duration
                        )

                        Log.d("123849375", sleep.toString())
                        onSave(sleep)
                        onDismiss()
                    } else {
                        Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text("Save", style = MaterialTheme.typography.labelLarge, color = AppColors.Slate)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(8.dp),
            ) {
                Text("Cancel", color = AppColors.Slate)
            }
        },
        shape = RoundedCornerShape(16.dp),
    )
}
