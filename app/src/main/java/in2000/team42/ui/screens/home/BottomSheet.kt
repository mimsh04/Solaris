package in2000.team42.ui.screens.home


import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import in2000.team42.ui.screens.home.bottomSheetKomp.Vinkelinputs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    val longitude = viewModel.longitude.collectAsState()
    val latitude = viewModel.latitude.collectAsState()
    val incline = viewModel.incline.collectAsState()
    val vinkel = viewModel.vinkel.collectAsState()

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
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .height(570.dp)
                    .padding(16.dp)
                    .padding(bottom = 74.dp)
            ) {
                item { Text("Bottom Sheet") }
                item {
                    Vinkelinputs(
                        incline = incline.value,
                        direction = vinkel.value,
                        onInclineChange = { viewModel.setIncline(it) },
                        onDirectionChange = { viewModel.setVinkel(it) }
                    )
                }
            }
        },
        sheetPeekHeight = 120.dp, // Høyde når kollapset, rett over NavBar. Må gjøres mer synlig?
        sheetSwipeEnabled = true,
    ) {

    }

}
