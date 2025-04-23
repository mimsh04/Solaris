package in2000.team42.ui.screens.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import in2000.team42.R
import in2000.team42.ui.screens.saved.project.ProjectViewModel


@Composable
fun SavedScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ProjectViewModel
) {
    val savedProjects = viewModel.savedProjects.collectAsState(initial = emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Lagrede Prosjekter",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start)
        )

        if (savedProjects.value.isEmpty()) {
            // Show empty state message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.nosaved),
                        contentDescription = "No saved projects",
                        modifier = Modifier.size(250.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Når du lagrer et prosjekt, vil det vises her.",
                        fontSize = 16.sp, // Updated: Slightly smaller font size
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Updated: Using themed onSurface with reduced opacity
                    )
                }
            }
        } else {
            // Display a scrollable list of saved projects
            LazyColumn {
                items(savedProjects.value) { project ->
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
