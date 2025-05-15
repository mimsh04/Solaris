package in2000.team42.ui.navbar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * An icon that flips 180° when activated (for navigation bar).
 *
 * @param modifier Optional styling.
 * @param isActive Whether the icon is in active state.
 * @param activeIcon Icon to show when active.
 * @param inactiveIcon Icon to show when inactive.
 * @param contentDescription Accessibility description.
 */
@Composable
fun FlipIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    contentDescription: String
) {
    // Animation for the flip effect
    val animationRotation by animateFloatAsState(
        targetValue = if (isActive) 180f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )
    Box(
        modifier = modifier.graphicsLayer { rotationY = animationRotation },
        contentAlignment = Alignment.Center,
    ) {
        // Show active/inactive icon based on rotation angle
        Icon(
            imageVector = if (animationRotation > 90f) activeIcon else inactiveIcon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}