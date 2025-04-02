package in2000.team42.model.faq

data class FAQ (val question: String, val answer: String){
    val faqItems = listOf(
        FAQ("Hvorfor utvikles denne appen?",
            "Appen skal hjelpe brukere med å beregne den forventede strømproduksjonen" +
                    " fra solcellepaneler basert på lokale klimadata, takforhold og solcellevirkningsgrad."),
        FAQ("Hvordan kan solceller fungere effektivt i Norge?",
            "Selv om man kanskje tror solceller fungerer best i varme klima," +
                    " er de mest effektive ved ca. -5°C og presterer dårlig ved temperaturer over 25°C." +
                    " I Sør-Norge kan solceller faktisk produsere mer energi enn i Afrika, så lenge det ikke er snødekke.")
    )
}