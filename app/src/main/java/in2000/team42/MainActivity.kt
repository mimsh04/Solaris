package in2000.team42

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            IN2000_team42Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { NavBar(navController) }
                    ) { innerPadding ->
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route){
                            HomeScreen(navController, Modifier
                                .padding(innerPadding),
                                viewModel = viewModel()
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController, Modifier
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