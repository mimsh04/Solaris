package in2000.team42.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.State
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

private val THEME_KEY = booleanPreferencesKey("is_dark_theme")

class ThemeManager(private val context: Context) {

    @Composable
    fun isDarkTheme(): State<Boolean> {
        val defaultDarkTheme = isSystemInDarkTheme() // Called in @Composable scope
        return context.dataStore.data
            .map { preferences ->
                preferences[THEME_KEY] ?: defaultDarkTheme
            }
            .collectAsState(initial = defaultDarkTheme)
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDark
        }
    }
}