package in2000.team42.ui.screens.settings.faq

data class FAQ (
    val question: String,
    val answer: String
)

val faqItems = listOf(
    FAQ("Hvorfor utvikles denne appen?",
        "Appen skal hjelpe brukere med å beregne den forventede strømproduksjonen" +
                " fra solcellepaneler basert på lokale klimadata, takforhold og solcellevirkningsgrad."),
    FAQ("Hvordan kan solceller fungere effektivt i Norge?",
        "Selv om man kanskje tror solceller fungerer best i varme klima," +
                " er de mest effektive ved ca. -5°C og presterer dårlig ved temperaturer over 25°C." +
                " I Sør-Norge kan solceller faktisk produsere mer energi enn i Afrika, så lenge det ikke er snødekke.")
    , FAQ("Kan jeg få støtte til solcelleinstallasjon?","Enova tilbyr støtte på opptil 32.500 kroner for strømproduksjon som kan tilbakeføres til strømnettet." +
            "Det er også mulig å få støtte for fritidsboliger."),
    FAQ("Kan jeg stole på beregningene i appen?","Appen baserer seg på tilgjengelige data og dokumenterte beregningsmodeller, " +
            "men verdiene kan avvike fra faktiske forhold." +
            "Det anbefales alltid å sammenligne resultatene med flere kilder.")
)