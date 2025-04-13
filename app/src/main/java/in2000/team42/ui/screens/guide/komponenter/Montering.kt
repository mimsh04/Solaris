package in2000.team42.ui.screens.guide.komponenter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in2000.team42.data.installasjon.model.MonteringData
import in2000.team42.data.installasjon.model.stegForsteg

@Composable
fun Montering(){

    Column(
        modifier=Modifier
            .padding(16.dp)
    ){
        LazyColumn(
            modifier=Modifier.fillMaxWidth()
        ) {
            items(stegForsteg) { steg ->
                ExpandableMonteringItem(monteringData = steg)
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }

//    CustomRoundedBox(
//        text=" 1. Vurder muligheten for solcelleanlegg",
//        fontWeight = FontWeight.Bold
//    ){
//        Text(
//            text="Før du monterer solcellepaneler, sjekk om det er tillatt i kommunen. " +
//                    "Noen steder krever søknad eller har restriksjoner." +
//                    " Sørg også for at taket er i god stand og har gode solforhold. " +
//                    "En solcelleforhandler kan hjelpe deg med vurderingen.",
//            modifier=Modifier.padding(start=25.dp)
//        )
//    }
//
//    Spacer(modifier= Modifier.height(30.dp))
//
//    CustomRoundedBox(
//        text="2. Velg forhandler og installatør",
//        height = 50.dp,
//        fontWeight = FontWeight.Bold
//    )
//
//    Spacer(modifier= Modifier.height(30.dp))
//
//    CustomRoundedBox(
//        text="3. Montering av paneler",
//        height = 50.dp,
//        fontWeight = FontWeight.Bold
//    )
//
//    Spacer(modifier= Modifier.height(30.dp))
//
//    CustomRoundedBox(
//        text="4. Sikring og tilkobling",
//        height = 50.dp,
//        fontWeight = FontWeight.Bold
//    )
}

@Composable
fun ExpandableMonteringItem(monteringData: MonteringData){
    var isExpanded by remember { mutableStateOf(false) }

    CustomRoundedBox(
        height = if (isExpanded) 200.dp else 45.dp,
        fontWeight = FontWeight.Bold
    ){
        Column(
            modifier = Modifier.padding(8.dp)
                .clickable { isExpanded = !isExpanded }

        ){
            Row(
                modifier=Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text=monteringData.question,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text= if (isExpanded) "-" else "+",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                )

                if (isExpanded){
                    Text(
                        text = monteringData.answer,
                        modifier = Modifier.padding(top=10.dp),
                        )
                }
            }


        }


    }

}

