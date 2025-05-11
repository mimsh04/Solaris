package in2000.team42.ui.screens.settings.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import in2000.team42.R
import in2000.team42.ui.screens.settings.faq.FaqDialog

@Composable
fun HelpSection(navController: NavController, showFAQ: Boolean, onShowFAQchange: (Boolean) -> Unit) {
    val gridState = rememberLazyGridState()

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ){Text(
        text = "Lurer du på noe?",
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(2) { index ->
            val text = if (index == 0) "Installasjons\nGuide" else "FAQs"

            val icon = when (index) {
                0 -> Icons.Default.Build
                1 -> Icons.Default.Info
                else -> Icons.Default.Edit
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        if (index == 0) {
                            navController.navigate("installasjonsguide")
                        } else {
                            onShowFAQchange(true)
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, start = 10.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 50.dp, top = 30.dp)
                )
            }
        }
    }
        }

    if (showFAQ) {
        FaqDialog(onDismiss = { onShowFAQchange(false) })
    }
}