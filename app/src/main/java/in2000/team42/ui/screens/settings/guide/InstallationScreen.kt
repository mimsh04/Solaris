package in2000.team42.ui.screens.settings.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import in2000.team42.R
import in2000.team42.ui.screens.guide.komponenter.Montering
import in2000.team42.ui.screens.settings.guide.komponenter.ArrowBack
import in2000.team42.ui.screens.settings.guide.komponenter.CustomRoundedBox
import in2000.team42.ui.screens.settings.guide.komponenter.Subtitle

@Composable
fun InstallasjonScreen(navController: NavController){
    Box(
        modifier=Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        LazyColumn(
            modifier=Modifier.fillMaxSize()
        ){
            item {
                Row {
                    ArrowBack(navController)

                    Text(
                        text = stringResource(R.string.installation_guide_title),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(start = 20.dp, top = 30.dp)
                    )

                }

            }
            item{Spacer(modifier=Modifier.height(30.dp))}

            item { Subtitle(stringResource(R.string.installation_when_self_install_subtitle)) }

            item{
                CustomRoundedBox(
                    text = stringResource(R.string.installation_self_install_description)
                )

            }

            item {
                Spacer(modifier=Modifier.height(30.dp))

                Subtitle(stringResource(R.string.installation_when_self_install_subtitle))

                CustomRoundedBox(
                    text = stringResource(R.string.installation_self_install_conditions),
                    height = 100.dp
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))

                Subtitle(stringResource(R.string.installation_mounting_steps_subtitle))

                CustomRoundedBox(
                    height = 300.dp
                ) {
                    Column{
                        Montering()
                    }
                }
            }

            item{
                Spacer(modifier = Modifier.height(100.dp))
            }

        }

    }
}