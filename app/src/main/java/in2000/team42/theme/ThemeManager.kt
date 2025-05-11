package in2000.team42.theme

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object ThemeManager {
    private lateinit var dataStore: DataStore<Preferences>
    private val THEME_KEY = booleanPreferencesKey("dark_theme")
    private var _isDarkTheme: MutableState<Boolean>? = null

    fun initialize(context: Context) {
        if (!::dataStore.isInitialized) {
            dataStore = context.dataStore
            _isDarkTheme = mutableStateOf(
                runBlocking {
                    // Default to light mode if no preference is stored
                    dataStore.data.first()[THEME_KEY] ?: false
                }
            )
        }
    }

    fun isDarkTheme(): State<Boolean> {
        check(_isDarkTheme != null) { "ThemeManager not initialized" }
        return _isDarkTheme!!
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDark
        }
        _isDarkTheme?.value = isDark
    }

    suspend fun toggleTheme() {
        val current = _isDarkTheme?.value ?: false
        setDarkTheme(!current)
    }
}