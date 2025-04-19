package in2000.team42.ui.screens.saved.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import in2000.team42.data.saved.SavedProjectDatabase
import in2000.team42.data.saved.SavedProjectEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {
    private val _savedProjects = MutableStateFlow<List<SavedProjectEntity>>(emptyList())
    val savedProjects = _savedProjects.asStateFlow()

    init {
        viewModelScope.launch {
            SavedProjectDatabase.getDatabase().savedProjectDao().getAllProjects()
                .collect { projects ->
                    _savedProjects.value = projects
                }
        }
    }

    /**
     * Finds a project by its ID.
     * @param id The project ID (as String).
     * @return The project if found, or `null`.
     */
    fun getProjectById(id: String): SavedProjectEntity? {
        return try {
            val longId = id.toLong()
            _savedProjects.value.firstOrNull { it.id == longId }
        } catch (e: NumberFormatException) {
            null
        }
    }
}