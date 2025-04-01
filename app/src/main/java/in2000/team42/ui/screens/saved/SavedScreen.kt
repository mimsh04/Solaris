package in2000.team42.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SavedScreen(navController: NavController,modifier: Modifier =Modifier){

Column(
    modifier=Modifier
        .fillMaxSize()
) {
    Spacer(modifier = Modifier.padding(20.dp))

    Text(
        text = "Lagrede Prosjekter",
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        modifier = Modifier
            .padding(top = 25.dp, bottom = 10.dp)
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
    )

        Box(
            modifier= Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray)
                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
        ){
            Text(
                text="Prosjekt 1",
                fontSize = 24.sp
            )
        }

    Spacer(modifier = Modifier.padding(20.dp))

        Box(
            modifier= Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray)
                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
        ){
            Text(
                text="Prosjekt 2",
                fontSize = 24.sp
            )
        }
}

}