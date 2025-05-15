package in2000.team42.ui.screens.home.bottomSheet.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import in2000.team42.R
import in2000.team42.ui.screens.home.HomeViewModel

@Composable
fun SaveButton(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit
) {
    var isSaved by remember { mutableStateOf(false) }
    val config = viewModel.configFlow.collectAsState()
    LaunchedEffect(config.value) {
        viewModel.isCurrentProjectSaved().collect { saved ->
            isSaved = saved
        }
    }

    Button(
        onClick = {
            if (isSaved) {
                viewModel.deleteCurrentProject()
                onShowSnackbar("Prosjekt er fjernet!")
            } else {
                viewModel.saveProject()
                onShowSnackbar("Prosjekt er lagret!")
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = if (isSaved) ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ) else ButtonDefaults.buttonColors(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (isSaved) Icons.Default.Delete else Icons.Default.FavoriteBorder,
                contentDescription = if (isSaved) "Fjern prosjekt ikon" else "Lagre prosjekt ikon",
                tint = LocalContentColor.current
            )
            Text(
                text = if (isSaved) stringResource(R.string.action_remove_project)
                else stringResource(R.string.action_save_project)
            )
        }
    }
}