package in2000.team42.ui.screens.settings.komponenter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in2000.team42.data.saved.SavedProjectEntity
import kotlin.random.Random

@Composable
fun YearlySummary(project: SavedProjectEntity) {
    // Simple random number that stays consistent per project
    val randomEnergy = remember(project.id) {
        Random(project.id.hashCode()).nextInt(2000, 5000)
    }

    Column(Modifier.padding(16.dp)
        .background(Color.Yellow)) {
        Text("Project Stats", fontWeight = FontWeight.Bold, color= Color.Black)
        Text("Energy: $randomEnergy kWh", color= Color.Black)
    }
}
