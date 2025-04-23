package in2000.team42.ui.navbar

import androidx.compose.ui.graphics.vector.ImageVector
import in2000.team42.ui.screens.Screen
/**
 * Represents a navigation item in the bottom bar.
 *
 * @param screen The destination screen.
 * @param title The label to display.
 * @param activeIcon Icon shown when selected.
 * @param inactiveIcon Icon shown when not selected.
 */
data class NavItem(
    val screen: Screen,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)