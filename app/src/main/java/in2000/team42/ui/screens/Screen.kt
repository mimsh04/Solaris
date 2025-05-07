package in2000.team42.ui.screens

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Saved : Screen("saved")
    data object Settings : Screen("settings/{projectId}")
    data object Guide:Screen("installasjonsguide")
}