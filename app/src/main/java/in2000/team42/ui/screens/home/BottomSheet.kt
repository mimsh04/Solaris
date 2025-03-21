package in2000.team42.ui.screens.home


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier
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
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")
                Text(text = "Bottom sheet")
                Text(text = "Dra meg opp eller ned da vel !?")


            }
        },
        sheetPeekHeight = 120.dp, // Høyde når kollapset, rett over NavBar. Må gjøres mer synlig?
        sheetSwipeEnabled = true,
    ) {

    }

}
