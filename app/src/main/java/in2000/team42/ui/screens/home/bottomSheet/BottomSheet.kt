package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
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
    val configState = viewModel.configFlow.collectAsState()
    val apiDataState = viewModel.apiDataFlow.collectAsState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for Greeting Visibility
    val showGreeting = remember { mutableStateOf(true) }

    val initialConfig = remember { configState.value }
    val initialApiData = remember { apiDataState.value }

    // Fjerner greeting ved bruk av info i bottomSheet
    LaunchedEffect(apiDataState.value, configState.value) {
        if (apiDataState.value != initialApiData || configState.value != initialConfig) {
            if (showGreeting.value) {
                showGreeting.value = false
            }
        }
    }

    val detents = remember { listOf(Peek, Medium, Closed) }
    val sheetState = rememberBottomSheetState(initialDetent = Medium, detents = detents)

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
            modifier = Modifier.fillMaxWidth(),
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
                    .fillMaxWidth()
                    .padding(top = 40.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 70.dp)
            ) {

                // Legger inn greeting først
                if (showGreeting.value) {
                    item {
                        GreetingContent()
                        Spacer(modifier = Modifier.height(16.dp)) // Spacer etter greeting
                    }
                }

                // Resten av innholdet
                item {
                    Vinkelinputs(
                        incline = configState.value.incline,
                        direction = configState.value.vinkel,
                        onInclineChange = { viewModel.setIncline(it) },
                        onDirectionChange = { viewModel.setVinkel(it) }
                    )
                }

                /*item {
                    StrommenContent()
                }*/

                item {
                    SolcelleInputs(viewModel)
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
                    )
                    { Text("Lagre prosjekt") }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}