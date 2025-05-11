package in2000.team42.ui.screens.settings.components

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import in2000.team42.R
import java.util.Locale
import androidx.core.content.edit

@Composable
fun LanguageSwitchButton(onLanguageChanged: () -> Unit) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(getCurrentLanguage(context)) }

    Button(
        onClick = {
            // Toggle between English and Norwegian (or your second language)
            val newLanguage = if (currentLanguage == "en") "no" else "en"
            updateLanguage(context, newLanguage)
            currentLanguage = newLanguage
            onLanguageChanged()
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = if (currentLanguage == "en") {
                "Switch to norwegian"
            } else {
                "Switch to english"
            },
            style = MaterialTheme.typography.labelLarge
        )
    }
}

fun getCurrentLanguage(context: Context): String {
    val locale = context.resources.configuration.locales[0]
    return locale.language
}

fun updateLanguage(context: Context, language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
    saveLanguagePreference(context, language)
    // Restart activity to apply locale changes
    (context as? Activity)?.recreate()
}

fun saveLanguagePreference(context: Context, language: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit() { putString("language", language) }
}

fun loadLanguagePreference(context: Context): String? {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getString("language", null)
}