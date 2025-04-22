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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.composables.core.DragIndication
import com.composables.core.SheetDetent
import com.composables.core.rememberBottomSheetState
import in2000.team42.ui.screens.home.HomeViewModel

// Detent definitions
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
    val config = viewModel.configFlow.collectAsState() // Collecting Config state
    val apiData = viewModel.apiDataFlow.collectAsState() // Collecting API data state
    val currentSheetDetent = viewModel.currentSheetDetent.collectAsState() // Collecting current SheetDetent
    val focusManager = LocalFocusManager.current

    val detents = listOf(
        Peek,
        Medium,
        Closed,
    )

    // All available heights for the sheet
    val sheetState = rememberBottomSheetState(initialDetent = Medium, detents = detents)

    LaunchedEffect(sheetState.currentDetent) {
        focusManager.clearFocus()
        // Update ViewModel's currentSheetDetent when the sheet's state changes
        viewModel.updateSheetDetent(sheetState.currentDetent.identifier)
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
            }
        }
    }
}
