package in2000.team42.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import in2000.team42.ui.screens.home.bottomSheet.BottomSheet
import in2000.team42.ui.screens.home.map.Map

@Composable

fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
) {
    Map( viewModel = viewModel)
    BottomSheet(modifier = modifier, viewModel = viewModel)
}