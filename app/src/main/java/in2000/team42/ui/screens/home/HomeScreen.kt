package in2000.team42.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import in2000.team42.ui.screens.home.bottomSheet.BottomSheet
import in2000.team42.ui.screens.home.map.Map

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = viewModel()
    Map(modifier = modifier, viewModel = viewModel)
    BottomSheet(modifier = modifier)
}