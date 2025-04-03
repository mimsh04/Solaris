package in2000.team42.ui.navbar

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
 fun BottomNavItem(
    modifier: Modifier = Modifier,
    navItem: NavItem,
    isSelected: Boolean,
) {
    val animatedHeight by animateDpAsState(
        targetValue = if (isSelected) 50.dp else 36.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 20.dp else 0.dp
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.7f
    )
    val animatedIconSize by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 24.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(animatedHeight)
                .shadow(
                    elevation = animatedElevation,
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface),
//                .then(
//                    if (isSelected){
//                        Modifier.border(2.dp,Color.LightGray, RoundedCornerShape(24.dp))
//                    }else{
//                        Modifier
//                    }
//                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            FlipIcon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
                    .padding(start = 12.dp)
                    .alpha(animatedAlpha)
                    .size(animatedIconSize),
                isActive = isSelected,
                activeIcon = navItem.activeIcon,
                inactiveIcon = navItem.inactiveIcon,
                contentDescription = navItem.title
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Text(
                    text = navItem.title,
                    modifier = Modifier.padding(start = 8.dp, end = 10.dp),
                    maxLines = 1,
                )
            }
        }
    }
}