package in2000.team42.ui.screens.home.bottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import in2000.team42.ui.screens.home.HomeViewModel

@Composable

fun SolcelleInputs(viewModel: HomeViewModel) {
    val solcelleEffekt = viewModel.solcelleEffekt.collectAsState()
    val areal = viewModel.areal.collectAsState()

    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        FloatInputField(
            "Effekt i %",
            solcelleEffekt.value,
            { viewModel.setSolcelleEffekt(it) },
            0..100,
        )
        FloatInputField(
            "Areal i m²",
            areal.value,
            { viewModel.setAreal(it) },
            1..200,
        )
    }
}