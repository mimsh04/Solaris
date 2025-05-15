package in2000.team42.ui.screens.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import in2000.team42.R
import in2000.team42.theme.IN2000_team42Theme


@Composable
fun RecommendedProducts () {
    val gridState= rememberLazyGridState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ){
        Text(
            text= stringResource(R.string.recommended_products_label),
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start),
            color = MaterialTheme.colorScheme.primary
        )

        LazyVerticalGrid(
            columns= GridCells.Fixed(2),
            state=gridState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable {
                            val url = "https://www.otovo.no/produkter/solcellepaneler/#content-row"
                            uriHandler.openUri(url)
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.otovo_logo),
                        contentDescription = "OTOVO solcellepaneler",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xff1e2439))
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Fit

                    )

                    Text(
                        text = stringResource(R.string.company_name_otovo),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                    )
                }
            }
            item{
                Box(
                    modifier= Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable {
                            val url = "https://sol.fjordkraft.no/"
                            uriHandler.openUri(url)
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fjordkraft_logo),
                        contentDescription = "Fjordkraft solcelle",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xffff531f))
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun AnbefalteProdukterPreview(){
    IN2000_team42Theme {
        RecommendedProducts()
    }
}
