package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.composables.core.DragIndication
import com.composables.core.SheetDetent
import com.composables.core.rememberBottomSheetState
import in2000.team42.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch


val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 1f
}

val Medium = SheetDetent(identifier = "medium") {
        containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

val Closed = SheetDetent(identifier = "closed") {
        containerHeight, sheetHeight ->
    containerHeight * 0.3f
}

@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val config = viewModel.configFlow.collectAsState() // Collecting Config state
    val apiData = viewModel.apiDataFlow.collectAsState() // Collecting API data state
    val focusManager = LocalFocusManager.current
    //for melding om prosjekt ble lagret
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val detents = listOf(
        Peek,
        Medium,
        Closed,
    )
    // alle høydene sheeten kan ligge
    val sheetState = rememberBottomSheetState(initialDetent = detents.find {
        it.identifier == config.value.bottomSheetDetent
    }!!, detents = detents)


    LaunchedEffect(sheetState.currentDetent) {
        focusManager.clearFocus()
        viewModel.setBottomSheetDetent(sheetState.currentDetent.identifier)
    }

    com.composables.core.BottomSheet(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(MaterialTheme.colorScheme.background),
        state = sheetState
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(1200.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            DragIndication(
                modifier = Modifier
                    .padding(top = 22.dp)
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                    .width(32.dp)
                    .height(4.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp)
                    .padding(top = 40.dp)
            ) {
                item {
                    AdresseFelt(config.value.adress)
                }
                item {
                    Vinkelinputs(
                        incline = config.value.incline,
                        direction = config.value.vinkel,
                        onInclineChange = { viewModel.setIncline(it) },
                        onDirectionChange = { viewModel.setVinkel(it) }
                    )
                }

                /*item {
                    StrommenContent()
                }*/

                item {
                    SolcelleInputs(viewModel) // Assuming this component accepts HomeViewModel directly
                }

                item {
                    Produksjon(apiData.value) // Assuming this component accepts HomeViewModel directly
                }
                item {
                    Button(onClick = {
                        viewModel.saveProject()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Prosjekt lagret!",
                                duration = SnackbarDuration.Short //hvor langt skal melding vises
                            )
                        }
                                     },
                        modifier= Modifier.padding(16.dp))
                    {Text("Lagre prosjekt") }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom=16.dp)
        )
    }
}
