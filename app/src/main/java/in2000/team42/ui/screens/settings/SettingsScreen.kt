package in2000.team42.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import in2000.team42.R
import in2000.team42.theme.ThemeManager
import in2000.team42.ui.screens.settings.components.HelpSection
import in2000.team42.ui.screens.settings.components.LanguageSwitchButton
import in2000.team42.ui.screens.settings.components.RecommendedProducts
import in2000.team42.ui.screens.settings.components.ThemeToggleButton
import in2000.team42.ui.screens.settings.faq.FaqDialog
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavHostController,
    currentLanguage: String,
    onLanguageChanged: (String) -> Unit
) {
    var showFAQ by remember { mutableStateOf(false) }
    val isDarkTheme by ThemeManager.isDarkTheme()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current // Localized context from MainActivity

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
                    text = stringResource(R.string.settings_screen_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )
            }

            Column(modifier = Modifier.height(180.dp)) {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = {
                        coroutineScope.launch {
                            ThemeManager.toggleTheme()
                        }
                    }
                )
                LanguageSwitchButton(
                    currentLanguage = currentLanguage,
                    onLanguageChanged = onLanguageChanged
                )
            }

            Box(modifier = Modifier.height(180.dp)) {
                RecommendedProducts()
            }

            Box(modifier = Modifier.height(250.dp)) {
                HelpSection(navController, { showFAQ = it })
            }
        }

        if (showFAQ) {
            key(currentLanguage) {
                FaqDialog(context = context, onDismiss = { showFAQ = false })
            }
        }
    }
}









