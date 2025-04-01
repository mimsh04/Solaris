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
import in2000.team42.ui.NavBar
import in2000.team42.ui.screens.Screen
import in2000.team42.ui.screens.home.HomeScreen
import in2000.team42.ui.screens.settings.SettingsScreen
import android.Manifest
import in2000.team42.ui.screens.saved.SavedScreen


class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.entries.all { it.value }
        if (!locationGranted) {
            // TODO: Fikse en popup om lokasjon ikke er skrudd på
        }
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
        setContent {
            val navController = rememberNavController()
            IN2000_team42Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { NavBar(navController) }
                    ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        enterTransition = { EnterTransition.None},
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }
                    ) {
                        composable(Screen.Home.route){
                            HomeScreen(navController, viewModel = viewModel(), modifier = Modifier
                                .padding(innerPadding)
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController, Modifier
                                .padding(innerPadding)
                            )
                        }

                        composable(Screen.Saved.route) {
                            SavedScreen(navController, Modifier
                                .padding(innerPadding)
                            )
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