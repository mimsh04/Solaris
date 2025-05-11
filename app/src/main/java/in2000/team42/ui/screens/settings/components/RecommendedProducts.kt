package in2000.team42.ui.screens.settings.components

import android.content.*
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import in2000.team42.R
import in2000.team42.theme.IN2000_team42Theme


@Composable
fun RecommendedProducts () {
    val gridState= rememberLazyGridState()
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ){
        Text(
            text= stringResource(R.string.label_recommended_products),
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
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.otovo.no/produkter/solcellepaneler/#content-row")
                            )
                            context.startActivity(intent)
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.otovo_logo),
                        contentDescription = "OTOVO solcellepaneler",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.FillBounds

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
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://sol.fjordkraft.no/")
                            )
                            context.startActivity(intent)
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fjordkraft_logo),
                        contentDescription = "Fjordkraft solcelle",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.FillBounds

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
