package in2000.team42.ui.screens.guide.komponenter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomRoundedBox(
    text:String? =null,
    height: Dp =200.dp,
    fontWeight: FontWeight=FontWeight.Normal,
    fontSize:TextUnit =16.sp,
    textColor: Color = Color.Black,
    backgroundColor:Color =Color.White,
    content: (@Composable () -> Unit)?=null
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(top = 16.dp)
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
            .background(backgroundColor)

    ){
        if(content != null){
            content()
        }
        else if(text!=null){
            Text(
                text=text,
                modifier=Modifier.padding(5.dp),
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = textColor
            )
        }

    }
}