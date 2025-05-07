package in2000.team42.data.installasjon.model

data class MonteringData(val question: String, val answer: String)

val installationSteps= listOf(
    MonteringData(" 1. Vurder muligheten for solcelleanlegg","Før du monterer solcellepaneler, sjekk om det er tillatt i kommunen. " +
            "Noen steder krever søknad eller har restriksjoner." +
            " Sørg også for at taket er i god stand og har gode solforhold. " +
            "En solcelleforhandler kan hjelpe deg med vurderingen."
    ),
    MonteringData(
        " 2. Velg forhandler og installatør",
        "Velg en forhandler og installatør som samarbeider for en smidig prosess. " +
                "Forhandleren vil hjelpe deg med å finne riktig solcelleanlegg, og de fleste jobber med sertifiserte installatører." +
                " Dette reduserer risikoen for misforståelser og konflikter."
    ),
    MonteringData(
        " 3. Montering av paneler",
        "Installatøren monterer først passende fester på taket, tilpasset materiale og helning." +
                " Deretter festes solcellepanelene sikkert, slik at de tåler både kraftig vind og tung snø. "

    ),
    MonteringData(
        " 4. Sikring og tilkobling",
        "Inverteren gjør solenergien fra panelene dine brukbar i hjemmets stikkontakter. " +
                "Installatøren plasserer inverteren på et passende sted og trekker kabler fra panelene, " +
                "som deretter kobles til inverteren."
    )
)