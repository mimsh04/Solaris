package in2000.team42.ui.screens.saved.project

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

/**
 * A swipeable project item with delete functionality.
 *
 * @param project The project to display.
 * @param onDeleteConfirmed Called when deletion is confirmed.
 * @param onClick Called when the item is clicked.
 */
@Composable
fun SwipeToDeleteItem(
    project: SavedProjectEntity,
    onDeleteConfirmed: (SavedProjectEntity) -> Unit,
    onClick: () -> Unit = {}
) {
    var swipeOffset by remember { mutableFloatStateOf(0f) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    val maxSwipeDistance = with(LocalDensity.current) { 80.dp.toPx() }
    val deleteButtonBackgroundColor = Color(0xFFEB5545)

    val animatedSwipeOffset by animateFloatAsState(
        targetValue = swipeOffset,
        animationSpec = tween(durationMillis = 300)
    )


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Delete button background (revealed when swiped)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(80.dp)
                .fillMaxHeight()
                .background(
                    color = deleteButtonBackgroundColor,
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                )
                .clickable { showDeleteConfirmationDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Slett prosjekt",
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier.size(30.dp)
            )
        }

        // Card with swipe functionality
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset { IntOffset(animatedSwipeOffset.roundToInt(), 0) }
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
            ProjectCard(
                project = project,
                onClick = onClick,
                isInSwipeContext = true
            )

        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(30.dp)
                .clickable {
                    swipeOffset = if (swipeOffset == 0f) -maxSwipeDistance else 0f
                }
        )

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