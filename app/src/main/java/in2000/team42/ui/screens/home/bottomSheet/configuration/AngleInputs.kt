package in2000.team42.ui.screens.home.bottomSheet.configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import in2000.team42.R

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

        FloatInputField(
            stringResource(R.string.homescreen_angle_input),
            incline,
            onInclineChange,
            0..90,
        )

        FloatInputField(
            stringResource(R.string.homescreen_direction_input),
            direction,
            onDirectionChange,
            -180..180,
        )
    }
}