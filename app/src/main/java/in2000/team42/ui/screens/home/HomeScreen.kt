package in2000.team42.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import in2000.team42.ui.screens.HKS.HKSViewModel

@Composable
fun HomeScreen(navController: NavHostController,
               modifier: Modifier = Modifier,
               homeViewModel: HomeViewModel, // HomeViewModel for hovedskjermen
               hksViewModel: HKSViewModel   // HKSViewModel for strømpriser
) {
    Map(modifier = modifier)
    BottomSheet(modifier = modifier, viewModel = hksViewModel)
}
































