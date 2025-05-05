package in2000.team42.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import in2000.team42.theme.IN2000_team42Theme
import in2000.team42.ui.navbar.*
import in2000.team42.ui.screens.Screen

private fun getNavItems(): List<NavItem> = listOf(
    NavItem(Screen.Saved, "Lagret", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    NavItem(Screen.Home, "Hjem", Icons.Filled.Home, Icons.Outlined.Home),
    NavItem(Screen.Settings, "Innstillinger", Icons.Filled.Settings, Icons.Outlined.Settings)
)


@Composable
fun NavBar(navController: NavHostController) {
    val currentScreen = getCurrentScreen(navController, getNavItems())
    val navItems = getNavItems()

    Box(
        modifier = Modifier
            .shadow(5.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .height(80.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navItems.forEach { navItem ->
                val isSelected = navItem == currentScreen
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    BottomNavItem(
                        modifier = Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            handleNavItemClick(navController, navItem, currentScreen)
                        },
                        navItem = navItem,
                        isSelected = isSelected
                    )
                }
            }
        }
    }
}


//Helper function to determine the current screen for navigation highlighting.
@Composable
private fun getCurrentScreen(navController: NavHostController,navItems: List<NavItem>): NavItem {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isProfileRelated = navController.previousBackStackEntry?.destination?.route?.startsWith("settings/") == true

    return when {
        currentRoute == Screen.Home.route -> navItems[1] // Home
        currentRoute == Screen.Saved.route -> navItems[0] // Saved
        currentRoute == Screen.Settings.route ||
                currentRoute?.startsWith("${Screen.Settings.route}/") == true ||
                isProfileRelated -> navItems[2] // Profile
        else -> navItems[1] // Default to Home
    }
}

//Handles navigation logic when a bottom bar item is clicked.
private fun handleNavItemClick(
    navController: NavHostController,
    navItem: NavItem,
    currentScreen:NavItem
){
    val isProfileRelated = navController.previousBackStackEntry?.destination?.route == Screen.Settings.route

    when {
        // Profile navigation
        navItem.screen == Screen.Settings && isProfileRelated -> {
            navController.popBackStack(Screen.Settings.route, false)
        }

        // navigation to Saved
        navItem.screen == Screen.Saved -> {
            navController.navigate(Screen.Saved.route) {
                popUpTo(navController.graph.id) { // Clear entire back stack
                    saveState = false
                }
                launchSingleTop = true
            }
        }

        // Normal navigation
        navItem != currentScreen -> {
            navController.navigate(navItem.screen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}


