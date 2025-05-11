package in2000.team42

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import in2000.team42.theme.IN2000_team42Theme
import in2000.team42.theme.ThemeManager
import in2000.team42.ui.navbar.NavBar
import in2000.team42.ui.screens.Screen
import in2000.team42.ui.screens.guide.InstallasjonScreen
import in2000.team42.ui.screens.home.HomeScreen
import in2000.team42.ui.screens.home.HomeViewModel
import in2000.team42.ui.screens.saved.SavedScreen
import in2000.team42.ui.screens.saved.project.ProjectViewModel
import in2000.team42.ui.screens.settings.SettingsScreen
import in2000.team42.data.saved.SavedProjectDatabase
import in2000.team42.utils.NetworkCheck
import android.Manifest
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import in2000.team42.utils.createLocalizedContext
import in2000.team42.utils.loadLanguagePreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.entries.all { it.value }
        // Handle permission denial if needed
    }

    private fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestPermissionLauncher.launch(permissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermissions()
        enableEdgeToEdge()
        SavedProjectDatabase.initialize(applicationContext)
        ThemeManager.initialize(applicationContext)
        setContent {
            // Initialize language state
            var language by remember { mutableStateOf(loadLanguagePreference(this) ?: "no") }
            // Create localized context
            val localizedContext = remember(language) { createLocalizedContext(this, language) }

            val navController = rememberNavController()
            val homeViewModel: HomeViewModel = viewModel()
            val projectViewModel: ProjectViewModel = viewModel()
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val isDarkTheme = ThemeManager.isDarkTheme().value

            // Network status observation
            LaunchedEffect(Unit) {
                NetworkCheck.observeNetworkStatus(this@MainActivity).collectLatest { isOnline ->
                    if (!isOnline) {
                        delay(300)
                        if (!NetworkCheck.isOnline(this@MainActivity)) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Ingen internett-tilkobling",
                                    duration = androidx.compose.material3.SnackbarDuration.Indefinite
                                )
                            }
                        }
                    } else {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            }

            IN2000_team42Theme(darkTheme = isDarkTheme, dynamicColor = false) {
                CompositionLocalProvider(LocalContext provides localizedContext) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = { NavBar(navController, LocalContext.current) },
                        containerColor = MaterialTheme.colorScheme.background,
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            enterTransition = { fadeIn() },
                            exitTransition = { fadeOut() },
                            popEnterTransition = { androidx.compose.animation.EnterTransition.None },
                            popExitTransition = { androidx.compose.animation.ExitTransition.None }
                        ) {
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    viewModel = homeViewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    navController = navController,
                                    currentLanguage = language,
                                    onLanguageChanged = { newLanguage ->
                                        language = newLanguage
                                    }
                                )
                            }
                            composable(Screen.Saved.route) {
                                SavedScreen(
                                    viewModel = projectViewModel,
                                    onProjectClick = { project ->
                                        homeViewModel.loadProject(project)
                                        navController.navigate(Screen.Home.route)
                                    }
                                )
                            }
                            composable(Screen.Guide.route) {
                                InstallasjonScreen(navController)
                            }
                        }
                    }
                }
            }
        }
    }
}