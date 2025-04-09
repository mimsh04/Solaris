package in2000.team42.ui.screens.guide.komponenter

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Montering(){

    CustomRoundedBox(
        text="1. Vurder muligheten for solcelleanlegg",
        height = 50.dp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier= Modifier.height(30.dp))

    CustomRoundedBox(
        text="2. Velg forhandler og installatør",
        height = 50.dp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier= Modifier.height(30.dp))

    CustomRoundedBox(
        text="3. Montering av paneler",
        height = 50.dp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier= Modifier.height(30.dp))

    CustomRoundedBox(
        text="4. Sikring og tilkobling",
        height = 50.dp,
        fontWeight = FontWeight.Bold
    )
}