package in2000.team42.ui.screens.saved.project

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import in2000.team42.R

/**
 * A confirmation dialog shown before deleting a project.
 *
 * @param onConfirm Called when the user confirms deletion.
 * @param onDismiss Called when the user cancels deletion.
 */
@Composable
fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_saved_project), color = MaterialTheme.colorScheme.onSurface) },
        text = { Text(stringResource(R.string.delete_project_confirmation), color = MaterialTheme.colorScheme.onSurface) },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.delete_project_confirmation_label), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.delete_project_confirmation_cancel), color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}