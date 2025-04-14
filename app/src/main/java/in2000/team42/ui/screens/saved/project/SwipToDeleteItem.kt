package in2000.team42.ui.screens.saved.project

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import in2000.team42.data.saved.SavedProjectEntity
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeToDeleteItem(
    project: SavedProjectEntity,
    onDeleteConfirmed: (SavedProjectEntity) -> Unit,
    onClick:()-> Unit= {}
) {
    var swipeOffset by remember { mutableStateOf(0f) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    val maxSwipeDistance = with(LocalDensity.current) { 80.dp.toPx() }
    val deleteButtonBackgroundColor = Color(0xFFEB5545)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(80.dp)
                .fillMaxHeight()
                .background(deleteButtonBackgroundColor)
                .clickable { showDeleteConfirmationDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Slett prosjekt",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        //swiping
        Box(
            modifier = Modifier
                .offset { IntOffset(swipeOffset.roundToInt(), 0) }
                .animateContentSize(animationSpec = tween(durationMillis = 300))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            swipeOffset = if (abs(swipeOffset) > maxSwipeDistance / 2) {
                                -maxSwipeDistance
                            } else {
                                0f
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            swipeOffset = (swipeOffset + dragAmount).coerceIn(-maxSwipeDistance, 0f)
                        }
                    )
                }
        ) {
            ProjectCard(project = project, onClick)
        }
    }

    if (showDeleteConfirmationDialog) {
        DeleteDialog(
            onConfirm = {
                onDeleteConfirmed(project)
                showDeleteConfirmationDialog = false
                swipeOffset = 0f
            },
            onDismiss = {
                showDeleteConfirmationDialog = false
                swipeOffset = 0f
            }
        )
    }
}