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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.composables.core.DragIndication
import com.composables.core.SheetDetent
import com.composables.core.rememberBottomSheetState
import in2000.team42.ui.screens.home.HomeViewModel
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
    val configState = viewModel.configFlow.collectAsState()
    val apiDataState = viewModel.apiDataFlow.collectAsState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val showGreeting = remember { mutableStateOf(true) }

    val initialConfig = remember { configState.value }
    val initialApiData = remember { apiDataState.value }

    val detents = remember { listOf(Peek, Medium, Closed) }
    val initialDetentObject = Medium
    val initialDetentIdentifier = initialDetentObject.identifier

    val sheetState = rememberBottomSheetState(
        initialDetent = initialDetentObject,
        detents = detents
    )

    // Fjerner greeting ved bruk av funksjoner

    LaunchedEffect(apiDataState.value, configState.value, initialApiData, initialConfig) {
        if (showGreeting.value && (apiDataState.value != initialApiData || configState.value != initialConfig)) {
            showGreeting.value = false
        }
    }

    LaunchedEffect(sheetState.currentDetent) {
        focusManager.clearFocus()
        viewModel.setBottomSheetDetent(sheetState.currentDetent.identifier)

        if (showGreeting.value && sheetState.currentDetent.identifier != initialDetentIdentifier) {
            showGreeting.value = false
        }
    }

    com.composables.core.BottomSheet(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(MaterialTheme.colorScheme.background),
        state = sheetState
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, bottom = 70.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Legger inn greeting først
                if (showGreeting.value) {
                    GreetingContent(
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        // BottomSheet innhold
                        item {
                            Vinkelinputs(
                                incline = configState.value.incline,
                                direction = configState.value.vinkel,
                                onInclineChange = { viewModel.setIncline(it) },
                                onDirectionChange = { viewModel.setVinkel(it) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        /*item {
                            StrommenContent()
                            Spacer(Modifier.height(8.dp))
                        }*/
                        item {
                            SolcelleInputs(viewModel)
                            Spacer(Modifier.height(8.dp))
                        }
                        item {
                            Produksjon(apiDataState.value)
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
                                modifier = Modifier.padding(top = 16.dp)
                            ) { Text("Lagre prosjekt") }
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }

            DragIndication(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 22.dp)
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                    .width(32.dp)
                    .height(4.dp)
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )
        }
    }
}