import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalContext
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
    var duration by remember { mutableStateOf(TextFieldValue(sleepTime?.duration?.toString() ?: "")) }
    var time by remember { mutableStateOf(sleepTime?.startTime ?: "00:00") }

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

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Start Time (HH:MM)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                )

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (Minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val durationMinutes = duration.text.toIntOrNull()
                if (name.text.isNotBlank() && durationMinutes != null && durationMinutes < 1440) {
                    val sleepTime = SleepTime(
                        id = sleepTime?.id,
                        scheduleId = sleepTime?.scheduleId,
                        name = name.text,
                        startTime = time,
                        duration = durationMinutes
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
