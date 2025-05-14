package in2000.team42.data.solarPanels

val standardPanel = SolarPanelModel(
    id = "standard",
    name = "Solcelleplater",
    efficiency = 19f, // 18%
    pricePerM2 = 2500f
)
val premiumPanel = SolarPanelModel(
    id = "solcelletakstein",
    name = "Solcelletakstein",
    efficiency = 17.5f, // 22%
    pricePerM2 = 4000f
)

val defaultPanels = listOf(standardPanel, premiumPanel)

data class SolarPanelModel(
    val id: String,
    val name: String,
    val efficiency: Float,
    val pricePerM2: Float
)