package in2000.team42.data.saved

import androidx.room.TypeConverter
import com.google.gson.Gson
import in2000.team42.ui.screens.home.Config

class ConfigTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun configToString(config: Config): String {
        return gson.toJson(config)
    }

    @TypeConverter
    fun stringToConfig(configString: String): Config {
        return gson.fromJson(configString, Config::class.java)
    }
}