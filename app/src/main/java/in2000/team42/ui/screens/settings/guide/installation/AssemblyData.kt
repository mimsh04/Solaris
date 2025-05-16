package in2000.team42.ui.screens.settings.guide.installation

import androidx.annotation.StringRes
import in2000.team42.R

data class AssemblyData(
    @StringRes val questionResId: Int,
    @StringRes val answerResId: Int
)

val installationSteps = listOf(
    AssemblyData(R.string.installation_step1_title, R.string.installation_step1_description),
    AssemblyData(R.string.installation_step2_title, R.string.installation_step2_description),
    AssemblyData(R.string.installation_step3_title, R.string.installation_step3_description),
    AssemblyData(R.string.installation_step4_title, R.string.installation_step4_description)
)