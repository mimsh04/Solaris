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
import androidx.compose.ui.unit.sp
import com.composables.core.DragIndication
import com.composables.core.SheetDetent
import com.composables.core.rememberBottomSheetState
import in2000.team42.data.solarPanels.defaultPanels
import in2000.team42.ui.screens.home.HomeViewModel
import in2000.team42.ui.screens.home.bottomSheet.charts.AllCharts
import in2000.team42.ui.screens.home.bottomSheet.configuration.AddressField
import in2000.team42.ui.screens.home.bottomSheet.configuration.AreaDisplay
import in2000.team42.ui.screens.home.bottomSheet.configuration.SolarPanelDropdown
import in2000.team42.ui.screens.home.bottomSheet.configuration.AngleInputs
import in2000.team42.ui.screens.home.bottomSheet.components.Production
import in2000.team42.ui.screens.home.bottomSheet.components.SaveButton
import in2000.team42.ui.screens.home.bottomSheet.components.UpdateApiButton
import kotlinx.coroutines.launch

val Peek = SheetDetent("peek") { containerHeight, _ ->
    containerHeight * 1f
}

val Medium = SheetDetent(identifier = "medium") { containerHeight, _ ->
    containerHeight * 0.6f
}

val Closed = SheetDetent(identifier = "closed") { containerHeight, _ ->
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

    // file name is the same as the composable
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
            if (config.value.address == "") {
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
                            AddressField(config.value.address)
                            AreaDisplay(
                                config.value.area,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                        }
                    }
                    item {
                        AngleInputs(
                            incline = config.value.incline,
                            direction = config.value.direction,
                            onInclineChange = { viewModel.setIncline(it) },
                            onDirectionChange = { viewModel.setDirection(it) }
                        )
                    }

                    item {
                        SolarPanelDropdown(
                            panelOptions = defaultPanels,
                            selectedPanel = config.value.selectedPanelModel,
                            onPanelSelected = { viewModel.setSelectedSolarPanel(it) },
                        )
                    }
                    item {
                        SaveButton(
                            viewModel = viewModel,
                            onShowSnackbar = { message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        // How long should the message be displayed
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                        )
                    }
                    item {
                        UpdateApiButton (
                            isEnabled = apiData.value.isLoading.not(),
                        ){
                            focusManager.clearFocus()
                            viewModel.clearSolarData()
                            viewModel.updateAllSolarData()
                            scope.launch {
                                sheetState.animateTo(Peek)
                            }
                        }
                    }
                    item {
                        Production(apiData.value)
                    }

                    item {
                        AllCharts(apiData.value)
                    }

                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            snackbar = { snackbarData ->
                Snackbar(
                    modifier = Modifier,
                    shape = MaterialTheme.shapes.medium,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    content = {
                        Text(
                            text = snackbarData.visuals.message,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                )
            }
        )
    }
}