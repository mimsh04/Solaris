package in2000.team42.ui.screens.guide.komponenter

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Subtitle(text:String){
    Text(
        text=text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier= Modifier
            .padding(start=10.dp)
    )
}