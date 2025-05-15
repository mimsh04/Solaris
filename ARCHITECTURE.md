# Applikasjonsarkitektur og Utviklingsguide

## 1. Introduksjon

Dette dokumentet er ment for utviklere som skal videreutvikle, sette seg inn i, og vedlikeholde denne applikasjonen. Målet er å gi en grundig forståelse av appens arkitektur, de teknologiske valgene som er gjort, og de etablerte kodepraksisene. Dette skal sikre konsistens og kvalitet i videre arbeid.

## 2. Arkitekturoversikt

Applikasjonen benytter en moderne Android-arkitektur basert på prinsipper fra [Googles anbefalte app-arkitektur](https://developer.android.com/topic/architecture). Kjernen i arkitekturen er **MVVM (Model-View-ViewModel)**, supplert med prinsipper for **UDF (Unidirectional Data Flow)** for å sikre en forutsigbar og vedlikeholdbar tilstandshåndtering.

Arkitekturen legger stor vekt på anerkjente objektorienterte prinsipper og designmønstre for å bygge en robust og vedlikeholdbar kodebase.

*   **MVVM (Model-View-ViewModel)**: Som nevnt er MVVM kjernen. Dette mønsteret separerer ansvarsområder tydelig:
    *   **View-laget (UI)**: Ansvarlig for å vise data på skjermen og håndtere brukerinteraksjon. Dette laget består av og kommer fra MainActivity som inneholder NavBar for navigering mellom screens og logikk for å håndtere de forskjellige screensa som inneholder Jetpack Compose Compasable UI-elementer.
    *   **ViewModel-laget**: Holder og forbereder data for UI-laget. ViewModel overlever konfigurasjonsendringer og er uavhengig av View-livssyklusen. Den eksponerer data via observerbare datakilder (f.eks. StateFlow) og håndterer brukerhandlinger.
    *   **Model-laget (Repository/Datasource)**: Abstraherer datakilder, inneholder matematiske kalkulasjoenr og logikk. Repositories henter data fra ulike kilder (APIer) og leverer det til ViewModels.
*   **UDF (Unidirectional Data Flow)**: Komplementerer MVVM ved å sikre at data flyter i én retning. Dette gir forutsigbar tilstandshåndtering og gjør det enklere å feilsøke. Hendelser går fra UI til ViewModel, som kan oppdatere tilstanden via model laget tilbake i ViewModel, som deretter observeres av UI-laget.

*   **Lav Kobling**:
    *   Den lagdelte MVVM-arkitekturen i seg selv fremmer lav kobling. UI kjenner kun ViewModel, og ViewModel kjenner kun Repository-laget. Lagene er dermed uavhengige av hverandres interne implementasjonsdetaljer.
    *   Modulær prosjektstruktur (beskrevet i seksjon 3) med separate pakker for `data` (videre inndelt per datakilde) og `ui` (med egne pakker per skjerm) reduserer avhengigheter mellom ulike deler av applikasjonen.

*   **Høy Kohesjon**:
    *   **Data-laget**: Hver pakke under `data` (se seksjon 3.1) samler all logikk for en spesifikk datakilde (modell, repository, datasource), noe som sikrer at relatert funksjonalitet er gruppert.
    *   **UI-laget**: Hver skjerm (`screen`) i `ui`-pakken (se seksjon 3.2) har typisk sin egen pakke med tilhørende ViewModel og UI-komponenter, noe som holder funksjonelt relaterte elementer samlet.

Dette bidrar til et system som er lettere å forstå, vedlikeholde, teste og videreutvikle, da endringer i en komponent har minimal påvirkning på andre.

Arkitekturen er lagdelt og består av:



## 3. Prosjektstruktur

Prosjektet er strukturert for å fremme modularitet og separasjon av ansvarsområder. I hovedsak er strukturen delt inn i 2 hovedeleter:

### 3.1 Data

For data område har vi strukturert appen slik at hver package i `data`  inneholder all logikk for en spesifikk datakilde. Dette inkluderer: 

*   **model**: Inneholder datamodeller for deserialisering og ferdig format av data.
*   **Repository**: Inneholder logikk for å hente og formatere. 
*   **Datasource**: Innheolder logikk for selve datakilden. API-kall og deserialisering av data.

I noen tilfeller kan en slik package være strukturert litt annereldes, dette er i tilfeller der flere datakilder blir kombinert for å regne ut nye verdier. Da er det ikke nødvendig med  `datasource` og `repository` da input dataene allerede er hentet og i riktig format for å bli brukt i kalkulasjonen.

### 3.2 UI 

For UI område er det en `ui`-pakke som inneholder alle UI-elementene. Her ligger `screens`-pakken som inneholder alle screens i appen og en `navbar` pakke som inneholder navbaren. Hver pakke i `screens` inneholder sin egen `ViewModel` der det er nødvendig og en Composable-funskjon som er screenen. Komponenter som brukes i denne screenen er også plassert i denne pakken, ofte i en `components`-pakke. Større komponenter han også ha nøstede pakker som inneholder flere Composable-funksjoner (eks. BottomSheet og Map på home-skjermen). 

For å legge til en ny screen i appen skal man lage en ny pakke i `screens`-pakken. Den nye pakken skal inneholde en Composable-funskjon, som er skjermen, samt legge til denne skjermen i `Screen`, som er en sealed klasse som ligger i `screens`-pakken. Den nye skjermen må legges til i `NavHost` i `MainActivitiy`. Om den nye skjermen skal ha egen `ViewModel` så skal denne instanseres i `MainActivity` og blir sendt inn i den nye skjermens Composable-funksjonen i `NavHost` som parameter.


## 4. Vedlikehold og Utvikling

### 4.1 Kodepraksis
*   **Koding Standard**: Følg Kotlin-kodestandardene og retningslinjene for Android-utvikling
*   **Språk**: Bruk engelsk for kommentarer og kode.
*   **Navnsetting**: Bruk beskrivende og meningsfulle navn for klasser, metoder og variabler. Unngå forkortelser med mindre de er kjente.
*   **Segmentering**: Del opp lange metoder og Composable-funksjoner i mindre, gjenbrukbare enheter. Dette gjør koden mer lesbar og lettere å teste.
*   **Kommentarer**: Kommenter komplekse eller uvanlige kodebiter. Unngå overflødige kommentarer som beskriver hva koden gjør.


### 4.2 Brukte Teknologier og Biblioteker
*   **Språk**: [Kotlin](https://kotlinlang.org/)
*   **Byggverktøy**: [Gradle](https://gradle.org/)
*   **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Kart løsning**: [MapBox](https://docs.mapbox.com/android/maps/guides/)
*   **Visualisering av grafer**: [Vico](https://github.com/patrykandpatrick/vico)
*   **Http klient**: [Ktor](https://ktor.io/)
*   **Bilde innlasting**: [Coil](https://coil-kt.github.io/coil/)
*   **Testing**: [JUnit](https://junit.org/junit5/), [Mockk](https://mockk.io/)

### 4.3 Andoid Versjoner
*   **Min SDK**: 26
*   **Mål SDK**: 35
