package in2000.team42.ui.screens.settings.komponenter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import in2000.team42.theme.IN2000_team42Theme

@Composable
fun AnbefalteProdukter () {
    val gridState= rememberLazyGridState()
    Column {
        Text(
            text="Anbefalte Produkter",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(top = 80.dp,bottom=10.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start),
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyVerticalGrid(
            columns= GridCells.Fixed(2),
            state=gridState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(2){index->
                Box(
                    modifier= Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                ){
                    Text(
                        text="High Effiency panels",
                        modifier= Modifier.align(Alignment.TopStart),
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .align(Alignment.BottomCenter)
                            .background(MaterialTheme.colorScheme.surface)
                            .clip(RoundedCornerShape(10.dp))
                    ){
                        Text(
                            text="Total Savings",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun AnbefalteProdukterPreview(){
    IN2000_team42Theme {
        AnbefalteProdukter()
    }
}
