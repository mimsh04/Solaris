package in2000.team42.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import in2000.team42.R
import in2000.team42.theme.ThemeManager
import in2000.team42.ui.screens.settings.faq.FaqDialog
import in2000.team42.ui.screens.settings.components.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavHostController) {
    var showFAQ by remember { mutableStateOf(false) }
    val isDarkTheme by ThemeManager.isDarkTheme() // Observe singleton state
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(if (showFAQ) Modifier.blur(10.dp) else Modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Innstillinger",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )
            }

            // Button for switching theme
            Box(modifier = Modifier.height(180.dp)) {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = {
                        coroutineScope.launch {
                            ThemeManager.toggleTheme()
                        }
                    }
                )
            }

            // Button for switching language
            Box(modifier = Modifier.height(180.dp)) {
                LanguageSwitchButton(
                    onLanguageChanged = {
                        // No additional logic needed here; LanguageSwitchButton handles recreation
                    }
                )
            }

            Box(modifier = Modifier.height(180.dp)) {
                RecommendedProducts()
            }

            Box(modifier = Modifier.height(250.dp)) {
                HelpSection(navController, showFAQ, { showFAQ = it })
            }
        }

        if (showFAQ) {
            FaqDialog(onDismiss = { showFAQ = false })
        }
    }
}