package in2000.team42.data.saved

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_projects")
data class SavedProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val incline: Float,
    val vinkel: Float
)
