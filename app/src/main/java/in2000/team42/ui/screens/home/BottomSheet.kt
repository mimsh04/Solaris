package in2000.team42.ui.screens.home


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import in2000.team42.ui.screens.HKS.HKSViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    viewModel: HKSViewModel
) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    // Fikser en bug der etter navigering tilbake til skjermen så er det
    // mulig å hjemme bottomsheten
    LaunchedEffect (scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden) {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,

        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp) // Maks høyde når ekspandert
                    .padding(16.dp)
            ) {
                Text(
                    text = "Hva koster strømmen?",
                    style = MaterialTheme.typography.headlineSmall
                )
                DatePicker(viewModel)
                TimePicker(viewModel)
                RegionSelector(viewModel)
                PriceDisplay(viewModel)
            }
        },
        sheetPeekHeight = 120.dp, // Høyde når kollapset, rett over NavBar. Må gjøres mer synlig?
        sheetSwipeEnabled = true,
    ) {

    }

}
