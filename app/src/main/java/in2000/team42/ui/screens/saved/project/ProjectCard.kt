package in2000.team42.ui.screens.saved.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import in2000.team42.data.saved.SavedProjectEntity

/**
 * A card displaying project details (address, coordinates, etc.).
 *
 * @param project The project data to display.
 * @param onClick Called when the card is clicked.
 */
@Composable
fun ProjectCard(project: SavedProjectEntity,
                onClick:()-> Unit={}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray)
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
            .clickable(onClick =onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Adresse: ${project.address}", fontSize = 18.sp)
            Text(text = "Latitude: ${project.latitude}", fontSize = 16.sp)
            Text(text = "Longitude: ${project.longitude}", fontSize = 16.sp)
            Text(text = "incline: ${project.incline}", fontSize = 16.sp)
            Text(text = "Vinkel: ${project.vinkel}", fontSize = 16.sp)
        }
    }
}