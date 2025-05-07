package in2000.team42

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import in2000.team42.theme.IN2000_team42Theme
import in2000.team42.ui.navbar.NavBar
import in2000.team42.ui.screens.Screen
import in2000.team42.ui.screens.home.HomeScreen
import in2000.team42.ui.screens.settings.SettingsScreen
import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import in2000.team42.ui.screens.guide.InstallasjonScreen
import in2000.team42.data.saved.*
import in2000.team42.ui.screens.home.HomeViewModel
import in2000.team42.ui.screens.saved.SavedScreen
import in2000.team42.ui.screens.saved.project.ProjectViewModel
import in2000.team42.utils.NetworkCheck
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.entries.all { it.value }
        //if (!locationGranted) {
            // TODO: Fikse en popup om lokasjon ikke er skrudd på
        //}
    }

    private fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestPermissionLauncher.launch(permissions)
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermissions()
        enableEdgeToEdge()
        SavedProjectDatabase.initialize(applicationContext)
        setContent {
            val navController = rememberNavController()

            val homeViewModel: HomeViewModel = viewModel()
            val projectViewModel: ProjectViewModel = viewModel()

            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            // Observes the network status and launches a snack bar if the user is offline
            LaunchedEffect(Unit) {
                NetworkCheck.observeNetworkStatus(this@MainActivity).collectLatest { isOnline ->
                    if (!isOnline) {
                        // Double-check network status after a short delay
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

            IN2000_team42Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { NavBar(navController) },
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = {SnackbarHost(snackbarHostState)}
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        enterTransition = { fadeIn()},
                        exitTransition = { fadeOut() },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }
                    ) {
                        composable(Screen.Home.route){
                            HomeScreen(
                                viewModel = homeViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController)
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IN2000_team42Theme {
        Greeting("Android")
    }
}