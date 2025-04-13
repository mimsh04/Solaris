package in2000.team42.ui.navbar

import androidx.compose.ui.graphics.vector.ImageVector
import in2000.team42.ui.screens.Screen

data class NavItem(
    val screen: Screen,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)