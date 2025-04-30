package in2000.team42.ui.screens.faq

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in2000.team42.model.faq.FAQ

@Composable
fun FAQItem(faq: FAQ){
    var expanded by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp)
    ){
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = faq.question,
                style= MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface

            )
            Text(
                text= if (expanded)"-" else "+",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start= 8.dp)
            )
        }

        if (expanded){
            Text(
                text = faq.answer,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top=10.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}