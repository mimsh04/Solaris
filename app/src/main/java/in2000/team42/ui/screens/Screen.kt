package in2000.team42.ui.screens

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Saved : Screen("saved")
    object Settings : Screen("settings/{projectId}"){
        fun createRoute(projectId: String) = "settings/$projectId"
        val baseRoute = "settings"}
    object Guide:Screen("installasjonsguide")
}