package in2000.team42.ui.screens.settings.components

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import in2000.team42.R
import java.util.Locale

@Composable
fun LanguageSwitchButton(currentLanguage: String, onLanguageChanged: (String) -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Norwegian option
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (currentLanguage == "no") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .clickable {
                    if (currentLanguage != "no") {
                        updateLanguage(context, "no")
                        onLanguageChanged("no")
                    }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = context.getString(R.string.language_norwegian),
                fontWeight = if (currentLanguage == "no") FontWeight.Bold else FontWeight.Normal,
                color = if (currentLanguage == "no") MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        // English option
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (currentLanguage == "en") MaterialTheme.colorScheme.primary //MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .clickable {
                    if (currentLanguage != "en") {
                        updateLanguage(context, "en")
                        onLanguageChanged("en")
                    }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = context.getString(R.string.language_english),
                fontWeight = if (currentLanguage == "en") FontWeight.Bold else FontWeight.Normal,
                color = if (currentLanguage == "en") MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
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
}

fun saveLanguagePreference(context: Context, language: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("language", language).apply()
}

fun loadLanguagePreference(context: Context): String? {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getString("language", null)
}

fun createLocalizedContext(baseContext: Context, language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = Configuration(baseContext.resources.configuration)
    config.setLocale(locale)
    return baseContext.createConfigurationContext(config)
}