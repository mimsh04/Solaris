package in2000.team42.ui.screens.saved

//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import in2000.team42.data.saved.*
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//
//class ProjectViewModel : ViewModel() {
//    private val savedProjectDao = SavedProjectDatabase.getDatabase().savedProjectDao()
//    private val _address = MutableStateFlow("")
//    val address = _address.asStateFlow()
//
//    fun setAddress(address: String) {
//        _address.value = address
//    }
//
//    fun saveProject(
//        latitude: Double,
//        longitude: Double,
//        incline: Float,
//        vinkel: Float
//    ) {
//        viewModelScope.launch {
//            savedProjectDao.insert(
//                SavedProjectEntity(
//                    address = _address.value,
//                    latitude = latitude,
//                    longitude = longitude,
//                    incline = incline,
//                    vinkel = vinkel
//                )
//            )
//        }
//    }
//
//    fun getSavedProjects(): Flow<List<SavedProjectEntity>> {
//        return savedProjectDao.getAllProjects()
//    }
//
//    fun deleteProject(project: SavedProjectEntity) {
//        viewModelScope.launch {
//            savedProjectDao.delete(project)
//        }
//    }
//}