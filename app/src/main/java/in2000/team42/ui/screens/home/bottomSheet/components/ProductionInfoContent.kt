package in2000.team42.ui.screens.home.bottomSheet.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

@Composable
fun ProductionInfoContent(yearlyKwh: Int) {
    val infoText = when {

        // The warnings are about the yearlyKwh being zero. That's the point
        yearlyKwh == 0 -> """
            0 kWh/år (ca. 0. kWh/dag)
            
            - Ingen energi produsert
                        
            → Ingen elektrisk hyttebruk
        """.trimIndent()
        yearlyKwh >= 10000 -> """
            $yearlyKwh kWh/år (ca. ${(yearlyKwh / 365.0).toInt()} kWh/dag)
            
            - Større kjøleskap/fryser
            - Liten oppvaskmaskin
            - Varmtvannstank (liten)
            - Vaskemaskin/tørketrommel
            - Elbillading (lett bruk)
            - Større strømverktøy
            - Elektrisk oppvarming (delvis)
            - Lade mobiltelefoner og nettbrett
            - Fullt utstyrt husholdning
            
            → Fullt elektrisk hytte, med fornuftig energibruk
        """.trimIndent()
        yearlyKwh in 5000..9999 -> """
            $yearlyKwh kWh/år (ca. ${(yearlyKwh / 365.0).toInt()} kWh/dag)
            
            - Større kjøleskap/fryser
            - Kaffetrakter, mikrobølgeovn, vannkoker
            - Liten oppvaskmaskin
            - Strømverktøy 
            - Vaskemaskin/tørketrommel (planlagt bruk)
            - Lade mobiltelefoner og nettbrett
            
            → Nær helårsbruk, men ikke full frihet
        """.trimIndent()
        else -> """
            $yearlyKwh kWh/år (ca. ${(yearlyKwh / 365.0).toInt()} kWh/dag)
            
            - Mindre kjøleskap/fryser
            - Kaffetrakter, mikrobølgeovn, vannkoker
            - Liten oppvaskmaskin
            - Strømverktøy (kortvarig)
            - Lade mobiltelefoner og nettbrett
            
            → Komfortabel hyttebruk, men fortsatt begrenset
        """.trimIndent()

    }

    Text(
        text = infoText,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Normal
    )
}