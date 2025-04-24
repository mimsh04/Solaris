package in2000.team42.model.solarPanels

// Panelvalg, ikke ekte paneler foreløpig
val standardPanel = SolarPanelModel(
    id = "standard",
    name = "Standard Panel",
    efficiency = 18f, // 18%
    pricePerM2 = 1500f
)
val premiumPanel = SolarPanelModel(
    id = "premium",
    name = "Premium Panel",
    efficiency = 22f, // 22%
    pricePerM2 = 2500f
)

val defaultPanels = listOf(standardPanel, premiumPanel)

data class SolarPanelModel(
    val id: String,
    val name: String,
    val efficiency: Float,
    val pricePerM2: Float
)