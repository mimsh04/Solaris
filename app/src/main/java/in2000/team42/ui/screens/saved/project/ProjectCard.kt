package in2000.team42.ui.screens.saved.project

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mapbox.maps.Style
import in2000.team42.R
import in2000.team42.data.mapboxStaticImage.getStaticImage
import in2000.team42.data.saved.SavedProjectEntity

/**
 * A card displaying project details (address, coordinates, etc.).
 *
 * @param project The project data to display.
 * @param onClick Called when the card is clicked.
 */
@SuppressLint("DefaultLocale")
@Composable
fun ProjectCard(
    project: SavedProjectEntity,
    onClick: () -> Unit = {},
    isInSwipeContext: Boolean
) {
    val mapUrl = getStaticImage(
        project.config.longitude,
        project.config.latitude,
        stringResource(R.string.mapbox_access_token),
        300,
        300,
        18.3f,
        project.config.polygon!!,
        if (isSystemInDarkTheme()) Style.DARK else Style.MAPBOX_STREETS
    )

    LaunchedEffect (mapUrl){
        Log.d("MapboxImage", "Map URL: $mapUrl")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            //.height(180.dp)
            .padding(if (isInSwipeContext) 0.dp else 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {

        Box(modifier=Modifier.fillMaxSize()){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {

                Text(
                    text = /*stringResource(R.string.saved_project_address_label)*/"Adresse " + project.config.address,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(mapUrl)
                            .crossfade(true)
                            .build(),
                        error = painterResource(id = R.drawable.ic_cloudy),
                        onError = {
                            Log.e("ProjectCard", "Error loading image: ${it.result.throwable}")
                        },
                        contentDescription = "Polygon preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .width(160.dp)
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))

                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Text(
                            text = "Angle: ${project.config.incline}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Direction: ${project.config.direction}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Areal: ${String.format("%.2f", project.config.area)} m²",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Panel: ${project.config.selectedPanelModel.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                }
            }
        }
    }
}