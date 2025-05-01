package in2000.team42.data.saved

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import in2000.team42.ui.screens.home.Config
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedProjectDao {
    @Insert
    suspend fun insert(project: SavedProjectEntity)

    @Query("SELECT * FROM saved_projects")
    fun getAllProjects(): Flow<List<SavedProjectEntity>>
    @Delete
    suspend fun delete(project: SavedProjectEntity)

    @Update
    suspend fun update(project: SavedProjectEntity)

    @Query("SELECT * FROM saved_projects WHERE config = :config LIMIT 1")
    suspend fun getProjectByConfig(config: Config): SavedProjectEntity?
}