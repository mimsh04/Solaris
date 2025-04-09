package in2000.team42.ui.screens.saved

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import in2000.team42.ui.screens.home.HomeViewModel
import in2000.team42.data.saved.*
import kotlin.math.abs
import kotlin.math.roundToInt

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

        LazyColumn {
            items(savedProjects) { project ->
                SwipeToDeleteItem(
                    project = project,
                    onDelete = { viewModel.deleteProject(project) }
                )
            }
        }
    }
}

@Composable
fun SwipeToDeleteItem(
    project: SavedProjectEntity,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val maxOffset = with(LocalDensity.current) { 80.dp.toPx() }
    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 300)
    )

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
                offsetX = 0f // Reset swipe position when dialog dismissed
            },
            title = { Text("Slett Prosjekt") },
            text = { Text("Er du sikker på at du vil slette dette prosjektet?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showConfirmDialog = false
                    }
                ) {
                    Text("Slett", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        offsetX = 0f // Reset swipe position
                    }
                ) {
                    Text("Avbryt")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Delete Button (hidden until swiped)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(80.dp)
                .fillMaxHeight()
                .background(Color.Red)
                .clickable { showConfirmDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Slett prosjekt",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        // Project Card
        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = if (abs(offsetX) > maxOffset / 2) {
                                -maxOffset // Keep it open if swiped enough
                            } else {
                                0f // Return to original position
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX = (offsetX + dragAmount).coerceIn(-maxOffset, 0f)
                        }
                    )
                }
        ) {
            ProjectCard(project = project)
        }
    }
}

@Composable
fun ProjectCard(project: SavedProjectEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray)
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
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