# Applikasjonsarkitektur og Utviklingsguide

## 1. Introduksjon

Dette dokumentet er ment for utviklere som skal videreutvikle, sette seg inn i, og vedlikeholde denne applikasjonen. Målet er å gi en grundig forståelse av appens arkitektur, de teknologiske valgene som er gjort, og de etablerte kodepraksisene. Dette skal sikre konsistens og kvalitet i videre arbeid.

## 2. Arkitekturoversikt

Applikasjonen benytter en moderne Android-arkitektur basert på prinsipper fra [Googles anbefalte app-arkitektur](https://developer.android.com/topic/architecture). Kjernen i arkitekturen er **MVVM (Model-View-ViewModel)**, supplert med prinsipper for **UDF (Unidirectional Data Flow)** for å sikre en forutsigbar og vedlikeholdbar tilstandshåndtering.

Arkitekturen er lagdelt og består av:

*   **UI-laget (View)**: Ansvarlig for å vise data på skjermen og håndtere brukerinteraksjon. Dette laget består av og kommer fra MainActivity som inneholder NavBar for navigering mellom screens og logikk for å håndtere de forskjellige screensa som inneholder Jetpack Compose Compasable UI-elementer.
*   **ViewModel-laget**: Holder og forbereder data for UI-laget. ViewModel overlever konfigurasjonsendringer og er uavhengig av View-livssyklusen. Den eksponerer data via observerbare datakilder (f.eks. StateFlow) og håndterer brukerhandlinger.
*   **Repository-laget/Datasource (Model)**: Abstraherer datakilder, inneholder matematiske kalkulasjoenr og logikk. Repositories henter data fra ulike kilder (APIer) og leverer det til ViewModels.

## 3. Prosjektstruktur

Prosjektet er strukturert for å fremme modularitet og separasjon av ansvarsområder. I hovedsak er strukturen delt inn i 2 hovedeleter:


### Data

For data område har vi strukturert appen slik at hver package i `data`  inneholder all logikk for en spesifikk datakilde. Dette inkluderer: 

*   **model**: Inneholder datamodeller for deserialisering og ferdig format av data.
*   **Repository**: Inneholder logikk for å hente og formatere. 
*   **Datasource**: Innheolder logikk for selve datakilden. API-kall og deserialisering av data.

I noen tilfeller kan en slik package være strukturert litt annereldes, dette er i tilfeller som når flere datakilder blir kombinert for å regne ut nye verdier. Da er det ikke nødvendig med  `datasource` og `repository` da input dataene allerede er hentet og i riktig format for å bli brukt i kalkulasjonen.

### UI

For UI område er det en `ui`-pakke som inneholder alle UI-elementene. Her er det også laget en `screens`-pakke som inneholder alle screens i appen og en `navbar` pakke som inneholder navbaren. Hver screen pakke inneholder sin egen `ViewModel` der det er nødvendig. Komponenter som brukes i denne screenen er også plassert i denne pakken. Større komponenter han også ha nøstede pakker som inneholder flere Composable-funksjoner, som eks BottomSheet og Map på home-skjermen. 

For å legge til en ny screen i appen skal man lage en ny pakke og med en screen samt legge til denne screenen i `Screen` som er sealed klasse som ligger i `screens`-pakken. Så må den legges til i `MainActivitiy`.

