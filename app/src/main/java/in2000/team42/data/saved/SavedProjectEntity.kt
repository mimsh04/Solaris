package in2000.team42.data.saved

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import in2000.team42.ui.screens.home.Config

@Entity(tableName = "saved_projects")
@TypeConverters(ConfigTypeConverter::class)
data class SavedProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val config: Config
){
    val stringId: String get() = id.toString()
}
