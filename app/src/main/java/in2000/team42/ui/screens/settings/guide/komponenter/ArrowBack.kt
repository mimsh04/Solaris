package in2000.team42.ui.screens.settings.guide.komponenter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ArrowBack(navController:NavController){
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back",
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .padding(start = 20.dp, top = 30.dp)
            .clickable {
                navController.popBackStack()
            }
    )
}