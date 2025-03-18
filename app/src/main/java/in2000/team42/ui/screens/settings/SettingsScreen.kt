package in2000.team42.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen (navController: NavHostController,modifier: Modifier = Modifier) {
    Column (modifier = modifier){
        Text("Settings screen")
    }
}