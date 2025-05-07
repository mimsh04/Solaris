package in2000.team42.ui.screens.home.bottomSheet.configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AngleInputs(
    modifier: Modifier = Modifier,
    incline: Float,
    direction: Float,
    onInclineChange: (Float) -> Unit,
    onDirectionChange: (Float) -> Unit
) {
    Row (modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        // Angle (Incline) Input
        FloatInputField(
            "Angle (0-90°)",
            incline,
            onInclineChange,
            0..90,
        )

        FloatInputField(
            "Direction (-180 - 180°)",
            direction,
            onDirectionChange,
            -180..180,
        )
    }
}