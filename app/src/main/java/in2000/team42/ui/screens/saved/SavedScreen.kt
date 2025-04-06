package in2000.team42.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import in2000.team42.ui.screens.home.HomeViewModel

@Composable
fun SavedScreen(navController: NavController, modifier: Modifier = Modifier, viewModel: HomeViewModel = viewModel()) {
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

        LazyColumn {
            items(savedProjects) { project ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                        .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Address: ${project.address}", fontSize = 18.sp)
                        Text(text = "Latitude: ${project.latitude}", fontSize = 16.sp)
                        Text(text = "Longitude: ${project.longitude}", fontSize = 16.sp)
                        Text(text = "Incline: ${project.incline}", fontSize = 16.sp)
                        Text(text = "Vinkel: ${project.vinkel}", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}