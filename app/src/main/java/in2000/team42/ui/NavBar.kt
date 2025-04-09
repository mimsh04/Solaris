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
import in2000.team42.ui.navbar.*
import in2000.team42.ui.screens.Screen

private fun getNavItems(): List<NavItem> = listOf(
    NavItem(Screen.Saved, "Lagret", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    NavItem(Screen.Home, "Hjem", Icons.Filled.Home, Icons.Outlined.Home),
    NavItem(Screen.Settings, "Profil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
)


@Composable
fun NavBar(navController: NavHostController) {
    val currentScreen = getCurrentScreen(navController, getNavItems())

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
            getNavItems().forEach { navItem ->
                val isSelected = navItem == currentScreen
                val animatedWeight by animateFloatAsState(
                    targetValue = if (isSelected) 1.8f else 1f,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                )

                Box(
                    modifier = Modifier.weight(animatedWeight),
                    contentAlignment = Alignment.Center,
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    BottomNavItem(
                        modifier = Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            handleNavItemClick(navController,navItem,currentScreen)
                        },
                        navItem = navItem,
                        isSelected = isSelected
                    )
                }
            }
        }
    }
}


//helper functions for cleaner navigation logic
@Composable
private fun getCurrentScreen(navController: NavHostController,navItems: List<NavItem>): NavItem {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isProfileRelated = navController.previousBackStackEntry?.destination?.route == Screen.Settings.route

    return when {
        currentRoute == Screen.Settings.route || isProfileRelated -> navItems[2] // Profile
        else -> navItems.find { it.screen.route == currentRoute } ?: navItems[1] // Home
    }
}


private fun handleNavItemClick(
    navController: NavHostController,
    navItem: NavItem,
    currentScreen:NavItem
){
    val isProfileRelated = navController.previousBackStackEntry?.destination?.route == Screen.Settings.route

    when {
        navItem.screen.route == Screen.Settings.route && isProfileRelated -> navController.popBackStack(Screen.Settings.route, false)
        navItem != currentScreen -> navController.navigate(navItem.screen.route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

}
