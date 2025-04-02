package in2000.team42.ui.screens.settings.komponenter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import in2000.team42.ui.screens.faq.FaqDialog
import in2000.team42.ui.screens.guide.InstallasjonScreen

@Composable
fun LurerDuPaaNoe(navController: NavController){
    val gridState= rememberLazyGridState()
    var showFAQ by remember{ mutableStateOf(false) }

    Text(
        text = "Lurer du på noe?",
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        modifier = Modifier
            .padding(top = 25.dp, bottom = 10.dp)
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
                    .background(Color.White)
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                    .clickable {
                        if(index==0){
                            navController.navigate("installasjonsguide")
                        }
                        else{
                            showFAQ=true
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 30.dp, start = 10.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)

                    )
                }
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 50.dp, top = 30.dp)
                )

            }

        }
    }

    if(showFAQ){
        FaqDialog(onDismiss = {showFAQ=false})
    }
}