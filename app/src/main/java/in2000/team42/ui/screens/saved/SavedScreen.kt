package in2000.team42.ui.screens.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import in2000.team42.ui.screens.Screen
import in2000.team42.ui.screens.home.HomeViewModel
import in2000.team42.ui.screens.saved.project.SwipeToDeleteItem

@Composable
fun SavedScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val savedProjects by viewModel.getSavedProjects().collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.padding(20.dp))

        Text(
            text = "Lagrede Prosjekter",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(top = 25.dp, bottom = 10.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start)
        )

        if (savedProjects.isEmpty()) {
            // Show empty state message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ingen lagrede prosjekter",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        } else {
            // Display a scrollable list of saved projects
            LazyColumn {
                items(savedProjects) { project ->
                    // Each project can be swiped to delete
                    SwipeToDeleteItem(
                        project = project,
                        onDeleteConfirmed = { viewModel.deleteProject(it) },
                        onClick = {  navController.navigate("settings/${project.id}")}
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
