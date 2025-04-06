package in2000.team42.ui.screens.saved
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedProjectEntity::class], version = 1)
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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}