package in2000.team42.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    Map(modifier = modifier)
    BottomSheet(modifier = modifier)
}
































