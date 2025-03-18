package in2000.team42.ui.screens

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Saved : Screen("saved")
    object Settings : Screen("settings")
}