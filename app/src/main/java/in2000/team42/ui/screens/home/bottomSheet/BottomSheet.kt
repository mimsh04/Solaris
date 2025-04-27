package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.composables.core.DragIndication
import com.composables.core.SheetDetent
import com.composables.core.rememberBottomSheetState
import in2000.team42.model.solarPanels.defaultPanels
import in2000.team42.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch

val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 1f
}

val Medium = SheetDetent(identifier = "medium") { containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

val Closed = SheetDetent(identifier = "closed") { containerHeight, sheetHeight ->
    containerHeight * 0.3f
}

@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val config = viewModel.configFlow.collectAsState()
    val apiData = viewModel.apiDataFlow.collectAsState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val detents = listOf(Peek, Medium, Closed)
    val sheetState = rememberBottomSheetState(
        initialDetent = detents.find { it.identifier == config.value.bottomSheetDetent }!!,
        detents = detents
    )

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
            modifier = Modifier
                .fillMaxWidth()
                .height(1200.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            DragIndication(
                modifier = Modifier
                    .padding(top = 22.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        RoundedCornerShape(100)
                    )
                    .width(32.dp)
                    .height(4.dp)
            )
            if (config.value.adress == "") {
                GreetingContent(modifier = Modifier.padding(top = 40.dp))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .padding(horizontal = 16.dp)
                        .padding(top = 40.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AdresseFelt(config.value.adress)
                            ArealFelt(
                                config.value.areal,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                    item {
                        Vinkelinputs(
                            incline = config.value.incline,
                            direction = config.value.vinkel,
                            onInclineChange = { viewModel.setIncline(it) },
                            onDirectionChange = { viewModel.setVinkel(it) }
                        )
                    }
                    item {
                        SolcelleDropdown(
                            panelOptions = defaultPanels,
                            selectedPanel = config.value.selectedPanelModel,
                            onPanelSelected = { viewModel.setSelectedSolarPanel(it) },
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        UpdateApiButton {
                            viewModel.updateAllApi()
                        }
                    }
                    item {
                        Produksjon(apiData.value)
                    }
                    item {
                        Button(
                            onClick = {
                                viewModel.saveProject()
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Prosjekt lagret!",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            modifier = Modifier.padding(16.dp)
                        ) { Text("Lagre prosjekt") }
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}