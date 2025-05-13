package in2000.team42.ui.screens.settings.guide.installation

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import in2000.team42.R

data class MonteringData(
    @StringRes val questionResId: Int,
    @StringRes val answerResId: Int
)

val installationSteps = listOf(
    MonteringData(R.string.installation_step1_title, R.string.installation_step1_description),
    MonteringData(R.string.installation_step2_title, R.string.installation_step2_description),
    MonteringData(R.string.installation_step3_title, R.string.installation_step3_description),
    MonteringData(R.string.installation_step4_title, R.string.installation_step4_description)
)