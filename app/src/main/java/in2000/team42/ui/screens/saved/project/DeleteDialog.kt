package in2000.team42.ui.screens.saved.project

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Slett Prosjekt") },
        text = { Text("Er du sikker på at du vil slette dette prosjektet?") },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Slett", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Avbryt")
            }
        }
    )
}