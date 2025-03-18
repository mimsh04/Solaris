package in2000.team42.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun HomeScreen (navController: NavHostController, modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    Column (modifier = modifier){
        Text("Home screen")
    }

}
