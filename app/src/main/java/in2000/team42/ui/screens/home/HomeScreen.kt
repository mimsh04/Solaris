package in2000.team42.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import in2000.team42.data.saved.SavedProjectEntity
import in2000.team42.ui.screens.home.bottomSheet.BottomSheet
import in2000.team42.ui.screens.home.map.Map

@Composable

fun HomeScreen(navController: NavHostController,
               modifier: Modifier = Modifier,
               viewModel: HomeViewModel,
               projectSharedState: MutableState<SavedProjectEntity?>
) {

    LaunchedEffect(projectSharedState.value) {
        projectSharedState.value?.let { project ->
            viewModel.loadProject(project)
            projectSharedState.value = null
        }
    }
    Map(modifier = modifier, viewModel = viewModel)
    BottomSheet(modifier = modifier, viewModel = viewModel)
}