package in2000.team42.ui.screens.home.bottomSheet

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import in2000.team42.ui.screens.home.HomeViewModel

@Composable

fun SolcelleInputs(viewModel: HomeViewModel) {
    val config = viewModel.configFlow.collectAsState()

    LaunchedEffect (config.value.areal){
        Log.d("SolcelleInputs", "Areal: ${config.value.areal}")
    }

    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        FloatInputField(
            "Effekt i %",
            config.value.solcelleEffekt,
            { viewModel.setSolcelleEffekt(it) },
            0..100,
        )
        FloatInputField(
            "Areal i m²",
            config.value.areal,
            { viewModel.setAreal(it) },
            1..10000,
        )
    }
}