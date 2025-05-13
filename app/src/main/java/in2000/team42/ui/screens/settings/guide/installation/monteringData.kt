package in2000.team42.ui.screens.settings.guide.installation

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import in2000.team42.R

data class MonteringData(
    @StringRes val questionResId: Int,
    @StringRes val answerResId: Int
)

val installationSteps = listOf(
    MonteringData(R.string.installation_step_1_title, R.string.installation_step_1_description),
    MonteringData(R.string.installation_step_2_title, R.string.installation_step_2_description),
    MonteringData(R.string.installation_step_3_title, R.string.installation_step_3_description),
    MonteringData(R.string.installation_step_4_title, R.string.installation_step_4_description)
)