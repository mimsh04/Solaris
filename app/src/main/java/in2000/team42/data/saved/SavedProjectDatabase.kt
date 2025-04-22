package in2000.team42.data.saved
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SavedProjectEntity::class], version = 2) // Increased from 1 to 2
@TypeConverters(ConfigTypeConverter::class) // Add the type converter
abstract class SavedProjectDatabase : RoomDatabase() {
    abstract fun savedProjectDao(): SavedProjectDao

    companion object {
        @Volatile
        private var INSTANCE: SavedProjectDatabase? = null
        private lateinit var appContext: Context

        fun initialize(context: Context) {
            appContext = context.applicationContext
        }

        fun getDatabase(): SavedProjectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    appContext,
                    SavedProjectDatabase::class.java,
                    "saved_project_database"
                )
                    .fallbackToDestructiveMigration() // Handles version change by recreating the database
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}