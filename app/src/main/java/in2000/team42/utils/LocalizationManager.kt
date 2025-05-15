package in2000.team42.utils

import android.content.Context
import android.os.LocaleList
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.ConfigurationCompat
import java.util.Locale

object LocalizationManager {
    private const val PREFS_NAME = "app_localization_prefs"
    private const val KEY_SELECTED_LANGUAGE = "selected_language"

    // Supported languages enum for type safety
    enum class SupportedLanguage(val code: String) {
        NORWEGIAN("no"),
        ENGLISH("en")
    }

    fun setLocale(context: Context, languageCode: String): Context {
        return updateResourcesLocale(context, Locale(languageCode))
    }

    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))

        return context.createConfigurationContext(configuration)
    }

    fun saveLanguagePreference(context: Context, languageCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SELECTED_LANGUAGE, languageCode)
            .apply()
    }

    fun getSelectedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SELECTED_LANGUAGE, getDeviceDefaultLanguage(context))
            ?: SupportedLanguage.ENGLISH.code
    }

    private fun getDeviceDefaultLanguage(context: Context): String {
        return ConfigurationCompat.getLocales(context.resources.configuration)[0]?.language
            ?: SupportedLanguage.ENGLISH.code
    }

    fun getSupportedLanguages(): List<SupportedLanguage> =
        SupportedLanguage.values().toList()
}