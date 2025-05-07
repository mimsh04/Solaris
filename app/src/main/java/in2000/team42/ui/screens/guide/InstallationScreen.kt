package in2000.team42.ui.screens.guide

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import in2000.team42.ui.screens.guide.komponenter.ArrowBack
import in2000.team42.ui.screens.guide.komponenter.CustomRoundedBox
import in2000.team42.ui.screens.guide.komponenter.Montering
import in2000.team42.ui.screens.guide.komponenter.Subtitle

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
                        text = "Solcellepanel Guide",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(start = 20.dp, top = 30.dp)
                    )

                }

            }
            item{Spacer(modifier=Modifier.height(30.dp))}

            item{Subtitle("Kan man montere solcellepaneler selv?")}

            item{
                CustomRoundedBox(
                    text = "Du kan ikke montere solcellepaneler selv, unntatt i visse tilfeller. " +
                            "Vi oppfordrer på det sterkeste å bruke fagfolk til større solcelleanlegg til boliger og hytter som er tilkoblet strømnettet.\n" +
                            "\n" +
                            "Dette sikrer at anlegget blir korrekt installert, " +
                            "uten brann- og helsefare. Du kan dessuten føle deg trygg på at solcellene fungerer optimalt."
                )

            }

            item{ Spacer(modifier=Modifier.height(30.dp))

                Subtitle("Når kan du montere selv?")

                CustomRoundedBox(
                    text= "1. Det er et frittstående solcelleanlegg\n" +
                            "2. Du er ikke tilkoblet strømnettet\n" +
                            "3. Solcelleanlegget er under 50V og 200VA\n" +
                            "4. Du ønsker ikke å søke om Enova-tilskudd",
                    height = 100.dp
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))

                Subtitle("Montering- steg for steg")

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